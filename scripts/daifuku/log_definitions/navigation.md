# AppLaunch

アプリ内の画面遷移に関するイベント

## navigate_to_screen

画面遷移したときのログ

- screen_name: !string 100
    - 遷移先の画面名
- arguments: !string? 2048
    - 遷移に使った引数

## navigate_to_dialog

ダイアログを表示したときのログ

- dialog_name: !string 100
    - 表示したダイアログの名前
- arguments: !string? 2048
    - 表示に使った引数
