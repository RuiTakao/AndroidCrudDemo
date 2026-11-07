"""静的なTodoデータを返すデモ用HTTPサーバー。"""

import json
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from urllib.parse import urlsplit


TODOS = [
    {"id": 1, "title": "買い物", "memo": "牛乳と卵を買う"},
    {"id": 2, "title": "掃除", "memo": "リビングを掃除する"},
    {"id": 3, "title": "洗濯", "memo": "タオルを洗う"},
    {"id": 4, "title": "読書", "memo": "本を30ページ読む"},
    {"id": 5, "title": "運動", "memo": "公園を30分散歩する"},
    {"id": 6, "title": "勉強", "memo": "Pythonの基礎を復習する"},
    {"id": 7, "title": "料理", "memo": "夕食にカレーを作る"},
    {"id": 8, "title": "メール確認", "memo": "届いたメールを確認する"},
    {"id": 9, "title": "予定確認", "memo": "明日の予定を確認する"},
    {"id": 10, "title": "日記", "memo": "今日の出来事を書く"},
]


class TodoHandler(BaseHTTPRequestHandler):
    def do_GET(self):
        path = urlsplit(self.path).path
        if path in ("/todo/getTodoList", "/todo/getTodoList/"):
            self.send_json(200, TODOS)
            return

        prefix = "/todo/getTodo/"
        if path.startswith(prefix):
            todo_id = path[len(prefix):].removesuffix("/")
            for todo in TODOS:
                if str(todo["id"]) == todo_id:
                    self.send_json(200, todo)
                    return

        self.send_json(404, {"error": "Not found"})

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
