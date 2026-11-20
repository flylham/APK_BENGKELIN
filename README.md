# Bengkelin - Android Native Starter (Malang Raya)

Aplikasi Android Native untuk pencarian bengkel terdekat di Malang Raya (Kota Malang, Kabupaten Malang, Kota Batu).
Arsitektur tanpa Firebase menggunakan REST API, Room SQLite, dan DataStore Preferences.

## Spesifikasi Teknis:
- **Bahasa**: Kotlin 2.0+
- **UI Framework**: Jetpack Compose dengan Material 3 Design
- **Arsitektur**: MVVM (Model-View-ViewModel) + Repository Pattern
- **Networking**: Retrofit 2 + OkHttp + Gson Converter
- **Database Lokal**: AndroidX Room (SQLite ORM) untuk caching offline
- **Manajemen Sesi**: AndroidX DataStore Preferences (JWT Bearer Token)
- **Backend**: REST API Server (Node.js/Express) + Skema SQL (PostgreSQL/MySQL)

## Kebijakan Akun & Otentikasi:
- **Pendaftaran Publik**: Khusus hanya untuk **PENGGUNA** (Pengendara motor/mobil).
- **Akun Administrator**: Disediakan dari awal secara sistem (Email: `admin@bengkelin.id` / Sandi: `admin123`). Tidak ada pendaftaran untuk Admin demi keamanan.
- **Pendaftaran Bengkel**: Dihapus dari formulir registrasi publik. Bengkel dikelola via Admin.

## Cara Menjalankan & Build APK di Android Studio:
1. Buka Android Studio (versi Hedgehog, Iguana, Jellyfish, Ladybug, Koala, atau Meerkat).
2. Pilih **File > Open**, lalu arahkan ke folder hasil ekstrak ini.
3. Lakukan **Gradle Sync** (pastikan Gradle JDK menggunakan Java 17 atau 21).
4. **Jalankan Langsung ke HP / Emulator**:
   - Colokkan HP Android via kabel USB (aktifkan USB Debugging) atau buat Emulator AVD.
   - Tekan tombol **Run (Shift + F10)** segitiga hijau di Android Studio.
5. **Cara Menghasilkan File APK (Bisa Dikirim & Diinstall di HP)**:
   - Di menu atas Android Studio: klik **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
   - Atau lewat Terminal: ketik `.gradlew assembleDebug` (Windows) atau `./gradlew assembleDebug` (Mac/Linux).
   - Hasil file APK terletak di: `app/build/outputs/apk/debug/app-debug.apk`.
   - Salin file `app-debug.apk` ke HP Android Anda lalu klik Install!

## Menjalankan Backend REST API (Opsional):
Folder `backend/` sudah dilengkapi dengan `package.json`, `server.js`, dan `schema.sql`:
```bash
cd backend
npm install
node server.js
```
(Server akan berjalan di http://localhost:5000)

Selamat mengembangkan Bengkelin Malang Raya!
