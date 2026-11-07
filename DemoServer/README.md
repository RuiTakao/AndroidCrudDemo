# Todoデモサーバー

Pythonの標準ライブラリで動作する、静的データのAPIサーバーです。

## 起動

この `DemoServer` ディレクトリで実行してください。Dockerの起動が必要です。

```sh
docker compose up --build -d
```

## API

```sh
curl http://localhost:8080/todo/getTodoList
```

`GET /todo/getTodoList` は、以下の形式のJSON配列を10件返します。
`id` は整数、`title` と `memo` は文字列です。データは毎回同じです。

```json
[
  {"id": 1, "title": "買い物", "memo": "牛乳と卵を買う"},
  {"id": 2, "title": "掃除", "memo": "リビングを掃除する"}
]
```

上記は先頭2件の抜粋です。レスポンスは `200 OK`、Content-Typeは
`application/json; charset=utf-8` です。存在しないパスは `404` を返します。

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
