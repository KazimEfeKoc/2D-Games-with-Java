# 2D Games with Java

Saf Java (Swing/AWT) kullanılarak, hiçbir ek kütüphaneye ihtiyaç duymadan yazılmış basit 2D oyunlar. Amaç, temel oyun programlama kavramlarını (game loop, çarpışma kontrolü, durum yönetimi, klavye girişi) sade ve okunabilir kodla göstermek.

## Oyunlar

| Oyun | Klasör | Açıklama |
|---|---|---|
| 🐍 Snake | [`snake/`](./snake) | Klasik yılan oyunu, duvardan çıkınca diğer taraftan devam eder (wrap-around) |
| 🧱 Tetris | [`tetris/`](./tetris) | 7 klasik tetromino, satır temizleme, seviye sistemi, hard drop |

## Gereksinimler

- JDK 17 veya üzeri (`java -version` ile kontrol edebilirsin)
- Ekstra kütüphane / build tool gerekmiyor — sadece JDK yeterli

## Çalıştırma

Snake tek bir `.java` dosyası olarak duruyor:

```bash
cd snake
javac SnakeGame.java
java SnakeGame
```

Tetris ise modüler yapıda, birden fazla dosyadan oluşuyor (`Tetromino.java`, `Board.java`, `GamePanel.java`, `TetrisGame.java`). Hepsini aynı klasörde derleyip çalıştır:

```bash
cd tetris
javac *.java
java TetrisGame
```

## Kontroller

### Snake
| Tuş | İşlev |
|---|---|
| Ok tuşları | Yön değiştir |
| SPACE | Oyun bitince yeniden başlat |

### Tetris
| Tuş | İşlev |
|---|---|
| ← / → | Parçayı yatayda hareket ettir |
| ↓ | Soft drop (bir kare aşağı it) |
| ↑ | Parçayı döndür |
| SPACE | Hard drop (anında en alta düşür) / oyun bitince yeniden başlat |
| P | Duraklat / devam et |

## Kullanılan teknik yaklaşım

Her iki oyun da aynı temel iskeleti kullanıyor:

- **`JPanel`** üzerinde `paintComponent(Graphics)` ile çizim
- **`javax.swing.Timer`** ile sabit aralıklarla tetiklenen bir game loop
- **`KeyListener`** ile klavye girişi
- Oyun mantığı ızgara (grid) tabanlı — piksel değil, hücre koordinatlarıyla çalışıyor

Tetris, Snake'e göre daha karmaşık olduğu için sorumluluklarına göre birkaç sınıfa bölündü:

| Dosya | Sorumluluğu |
|---|---|
| `Tetromino.java` | Parça verisi (şekiller, renkler) + bir parçanın kendi durumu (tip, döndürme, konum) |
| `Board.java` | Tahtanın durumu, çarpışma kontrolü, kilitleme, satır temizleme |
| `GamePanel.java` | Game loop, klavye girişi, çizim |
| `TetrisGame.java` | Sadece `main()` — pencereyi açan giriş noktası |

## Lisans

Bu proje eğitim amaçlıdır, dilediğin gibi kullanabilir, değiştirebilir, paylaşabilirsin.
