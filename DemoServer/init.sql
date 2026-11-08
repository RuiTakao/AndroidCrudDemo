SET NAMES utf8mb4;
CREATE TABLE todos (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    memo TEXT NOT NULL
) CHARACTER SET utf8mb4;

INSERT INTO todos (id, title, memo) VALUES
(1, '買い物', '牛乳と卵を買う'),
(2, '掃除', 'リビングを掃除する'),
(3, '洗濯', 'タオルを洗う'),
(4, '読書', '本を30ページ読む'),
(5, '運動', '公園を30分散歩する'),
(6, '勉強', 'Pythonの基礎を復習する'),
(7, '料理', '夕食にカレーを作る'),
(8, 'メール確認', '届いたメールを確認する'),
(9, '予定確認', '明日の予定を確認する'),
(10, '日記', '今日の出来事を書く');
