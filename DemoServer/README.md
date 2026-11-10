# Todoデモサーバー

Python + PyMySQL と MySQL 8.4 で動作するTodo APIサーバーです。

## 起動

この `DemoServer` ディレクトリで実行してください。Dockerの起動が必要です。

```sh
docker compose up --build -d
```

## API

```sh
curl http://localhost:8080/todo/getTodoList
```

`GET /todo/getTodoList` は、登録済みの全データをID昇順のJSON配列で返します。
初回起動時にサンプル10件を登録します。`id` は整数、`title` と `memo` は文字列です。

```json
[
  {"id": 1, "title": "買い物", "memo": "牛乳と卵を買う"},
  {"id": 2, "title": "掃除", "memo": "リビングを掃除する"}
]
```

上記は先頭2件の抜粋です。レスポンスは `200 OK`、Content-Typeは
`application/json; charset=utf-8` です。存在しないパスは `404` を返します。

### 新規登録

```sh
curl -X POST http://localhost:8000/todo/create \
  -H 'Content-Type: application/json' \
  -d '{"title":"新しいTodo","memo":"登録する内容"}'
```

`POST /todo/create` はMySQLに登録し、`201 Created` と登録済みのJSONオブジェクトを返します。
`id` はサーバーで自動採番します。末尾の `/` にも対応します。

```json
{"id": 11, "title": "新しいTodo", "memo": "登録する内容"}
```

`title` は必須の文字列（空白のみ不可、最大255文字）、`memo` は必須の文字列
（空文字可、UTF-8で最大65535バイト）です。JSON本文は最大100000バイトです。
不正な入力は `400`、JSON以外のContent-Typeは `415`、DB接続エラーは `503` を返します。
登録したデータは一覧・ID指定取得の両方に反映されます。

### 更新

```sh
curl -X PUT http://localhost:8000/todo/update \
  -H 'Content-Type: application/json' \
  -d '{"id":1,"title":"更新したタイトル","memo":"更新したメモ"}'
```

`PUT /todo/update` はJSON本文の `id` をキーに、`title` と `memo` の両方を更新します。
3項目とも必須です。`id` は1〜2147483647の整数、`title` と `memo` は新規登録と同じ入力条件です。
末尾の `/` にも対応します。成功時は `200 OK` と更新後のJSONオブジェクトを返します。
同じ内容で再度更新した場合も `200` です。存在しないIDは新規作成せず `404` を返します。
不正な入力は `400`、JSON以外のContent-Typeは `415`、DB接続エラーは `503` です。
AndroidからもHTTPメソッドに `PUT` を指定してください。

### 削除

```sh
curl -X DELETE http://localhost:8000/todo/delete \
  -H 'Content-Type: application/json' \
  -d '{"id":1}'
```

`DELETE /todo/delete` はJSON本文の `id` で指定した1件をMySQLから削除します。
末尾の `/` にも対応します。`id` は1〜2147483647の整数が必須です。
成功時は `200 OK` と `{"id":1,"deleted":true}` を返します。
存在しないIDは `404`、不正な入力は `400`、JSON以外のContent-Typeは `415`、
DB接続エラーは `503` です。削除結果は一覧・ID指定取得にも反映されます。

### ID指定で1件取得

```sh
curl http://localhost:8080/todo/getTodo/1
```

`GET /todo/getTodo/{id}` は、指定したIDのJSONオブジェクトを返します。
末尾に `/` がある場合も対応します。存在しないIDは `404` を返します。

```json
{"id": 1, "title": "買い物", "memo": "牛乳と卵を買う"}
```

Android標準エミュレーターからホストPCへアクセスする場合のURL:

```text
http://10.0.2.2:8000/todo/getTodoList/
```

Androidアプリではインターネット権限と、開発用HTTP通信の許可設定が必要です。
ホスト側は8000番と8080番の両方で公開しています。URL末尾の `/` は有無の両方に対応します。
実機から接続する場合は、同じネットワーク上のPCのLAN IPアドレスを使用してください。

## 停止

```sh
docker compose down
```

## MySQL

DB名は `todo_demo`、テーブル名は `todos` です。
`id INT AUTO_INCREMENT PRIMARY KEY`、`title VARCHAR(255) NOT NULL`、
`memo TEXT NOT NULL` を定義し、日本語・絵文字に対応する `utf8mb4` を使用します。
DBはCompose内の `mysql:3306` から利用し、ホストにはポートを公開しません。

開発用の接続ユーザー・パスワードは `compose.yaml` に定義しています。
APIは `todo_user` を使用します。DBを操作する場合は次を実行し、
パスワード `todo_demo_password` を入力してください。

```sh
docker compose exec mysql mysql -u todo_user -p todo_demo
```

データはDockerの名前付きボリュームに保存され、通常の停止・再起動では保持されます。
`init.sql` はDBの初回作成時のみ実行されます。
`docker compose down -v` は登録データも削除するため、初期化したい場合のみ実行してください。

初期化方法の参照: [MySQL公式Dockerイメージ](https://hub.docker.com/_/mysql)
