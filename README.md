# Kelime Öğrenme Uygulaması

Bu uygulama, 6 sefer tekrar prensibine dayalı bir kelime öğrenme sistemidir. JavaFX kullanılarak geliştirilmiş masaüstü uygulamasıdır.

## Özellikler

- Kullanıcı kayıt/giriş sistemi
- Kelime ekleme (İngilizce kelime, Türkçe karşılık, örnek cümleler, görsel ve ses)
- 6 sefer tekrar prensibine dayalı öğrenme sistemi
- Günlük kelime çalışma limiti ayarlama
- İlerleme takibi ve istatistikler
- Görsel ve ses desteği

## Teknik Detaylar

- Java 17
- JavaFX 17
- Hibernate 6.2
- H2 Database
- Maven

## Kurulum

1. Java 17 JDK'yı yükleyin
2. Maven'i yükleyin
3. Projeyi klonlayın
4. Proje dizininde aşağıdaki komutu çalıştırın:
   ```bash
   mvn clean install
   ```
5. Uygulamayı başlatmak için:
   ```bash
   mvn javafx:run
   ```

## Veritabanı Şeması

### User
- id (Long)
- username (String)
- password (String)
- newWordsPerDay (Integer)

### Word
- id (Long)
- englishWord (String)
- turkishWord (String)
- imageUrl (String)
- audioUrl (String)

### WordSample
- id (Long)
- word (Word)
- sample (String)

### WordProgress
- id (Long)
- user (User)
- word (Word)
- correctCount (Integer)
- nextReviewDate (LocalDateTime)
- lastReviewDate (LocalDateTime)
- isLearned (Boolean)

## Geliştirme

Projeyi geliştirmek için:

1. IDE'nizde projeyi açın
2. Maven bağımlılıklarının yüklenmesini bekleyin
3. `Main.java` dosyasını çalıştırın

## Katkıda Bulunma

1. Bu depoyu fork edin
2. Yeni bir özellik dalı oluşturun (`git checkout -b feature/amazing-feature`)
3. Değişikliklerinizi commit edin (`git commit -m 'Add some amazing feature'`)
4. Dalınıza push edin (`git push origin feature/amazing-feature`)
5. Bir Pull Request oluşturun

## Lisans

Bu proje MIT lisansı altında lisanslanmıştır. Detaylar için `LICENSE` dosyasına bakın. 