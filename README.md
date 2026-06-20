# GtaTeleport
GTA っぽいテレポートを再現する Paper プラグイン

参考: https://twitter.com/hookuru_/status/2066780862507798978

## コマンド

### 座標指定テレポート
- `/gtatp <x> <z>`: 指定した座標 (Y=0) へ移動します。
- `/gtatp <x> <y> <z>`: 指定した座標へ移動します。(厳密には Y は無視されます。)
- **パーミッション**: `gtateleport.coordinates`

### 特殊位置へのテレポート
- `/gtatp bed`: 最後に寝た場所、またはリスポーン地点に移動します。
  - **パーミッション**: `gtateleport.bed`
- `/gtatp spawn`: ワールドのスポーン地点に移動します。
  - **パーミッション**: `gtateleport.spawn`
- `/gtatp <プレイヤー名>`: 指定したプレイヤーのベッド位置に移動します。
  - **パーミッション**: `gtateleport.others`

**注**: すべてのコマンドは **Overworld でのみ** 使用可能です。

https://github.com/user-attachments/assets/d36d0a85-50fe-4536-95e0-a70bc8d29ffd
