"""MySQLにTodoを保存するデモ用HTTPサーバー。"""

import json
import os
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from urllib.parse import urlsplit

import pymysql


def connect_db():
    return pymysql.connect(
        host=os.environ["DB_HOST"],
        user=os.environ["DB_USER"],
        password=os.environ["DB_PASSWORD"],
        database=os.environ["DB_NAME"],
        charset="utf8mb4",
        cursorclass=pymysql.cursors.DictCursor,
        connect_timeout=5,
        read_timeout=10,
        write_timeout=10,
    )


class TodoHandler(BaseHTTPRequestHandler):
    def do_GET(self):
        path = urlsplit(self.path).path
        sql = None
        params = ()
        if path in ("/todo/getTodoList", "/todo/getTodoList/"):
            sql = "SELECT id, title, memo FROM todos ORDER BY id"
        elif path.startswith("/todo/getTodo/"):
            todo_id = path[len("/todo/getTodo/"):].removesuffix("/")
            if todo_id.isascii() and todo_id.isdecimal() and len(todo_id) <= 10:
                sql = "SELECT id, title, memo FROM todos WHERE id = %s"
                params = (int(todo_id),)
        if sql is None:
            self.send_json(404, {"error": "Not found"})
            return
        try:
            with connect_db() as db:
                with db.cursor() as cursor:
                    cursor.execute(sql, params)
                    data = cursor.fetchone() if params else cursor.fetchall()
        except pymysql.MySQLError:
            self.send_json(503, {"error": "Database unavailable"})
            return
        if data is None:
            self.send_json(404, {"error": "Not found"})
        else:
            self.send_json(200, data)

    def do_POST(self):
        if urlsplit(self.path).path not in ("/todo/create", "/todo/create/"):
            self.send_json(404, {"error": "Not found"})
            return
        data = self.read_json_object()
        if data is None:
            return
        title, memo = data.get("title"), data.get("memo")
        if not isinstance(title, str) or not title.strip() or len(title) > 255:
            self.send_json(400, {"error": "title must be a non-empty string of at most 255 characters"})
            return
        if not isinstance(memo, str) or len(memo.encode("utf-8", errors="replace")) > 65535:
            self.send_json(400, {"error": "memo must be a string of at most 65535 UTF-8 bytes"})
            return
        try:
            title.encode("utf-8")
            memo.encode("utf-8")
        except UnicodeEncodeError:
            self.send_json(400, {"error": "Invalid Unicode text"})
            return
        try:
            with connect_db() as db:
                with db.cursor() as cursor:
                    cursor.execute("INSERT INTO todos (title, memo) VALUES (%s, %s)", (title, memo))
                    todo_id = cursor.lastrowid
                db.commit()
        except pymysql.MySQLError:
            self.send_json(503, {"error": "Database unavailable"})
            return
        self.send_json(201, {"id": todo_id, "title": title, "memo": memo})

    def do_DELETE(self):
        if urlsplit(self.path).path not in ("/todo/delete", "/todo/delete/"):
            self.send_json(404, {"error": "Not found"})
            return
        data = self.read_json_object()
        if data is None:
            return
        todo_id = data.get("id")
        if type(todo_id) is not int or not 1 <= todo_id <= 2147483647:
            self.send_json(400, {"error": "id must be an integer between 1 and 2147483647"})
            return
        try:
            with connect_db() as db:
                with db.cursor() as cursor:
                    cursor.execute("DELETE FROM todos WHERE id = %s", (todo_id,))
                    deleted = cursor.rowcount
                db.commit()
        except pymysql.MySQLError:
            self.send_json(503, {"error": "Database unavailable"})
            return
        if deleted == 0:
            self.send_json(404, {"error": "Not found"})
        else:
            self.send_json(200, {"id": todo_id, "deleted": True})

    def read_json_object(self):
        if self.headers.get_content_type() != "application/json":
            self.send_json(415, {"error": "Content-Type must be application/json"})
            return
        if self.headers.get("Transfer-Encoding"):
            self.send_json(400, {"error": "Use Content-Length instead of Transfer-Encoding"})
            return
        try:
            length = int(self.headers.get("Content-Length", "0"))
            if not 0 < length <= 100000:
                raise ValueError
            raw = self.rfile.read(length)
            data = json.loads(raw.decode("utf-8"))
        except (ValueError, UnicodeDecodeError):
            self.send_json(400, {"error": "Invalid JSON body or Content-Length"})
            return
        if not isinstance(data, dict):
            self.send_json(400, {"error": "JSON body must be an object"})
            return
        return data

    def send_json(self, status, data):
        body = json.dumps(data, ensure_ascii=False).encode("utf-8")
        self.send_response(status)
        self.send_header("Content-Type", "application/json; charset=utf-8")
        self.send_header("Content-Length", str(len(body)))
        self.end_headers()
        self.wfile.write(body)


if __name__ == "__main__":
    server = ThreadingHTTPServer(("0.0.0.0", 8000), TodoHandler)
    print("Todo server listening on port 8000", flush=True)
    server.serve_forever()
