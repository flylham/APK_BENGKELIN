/**
 * Server REST API Backend Bengkelin (Node.js + Express + JWT + SQLite/PostgreSQL)
 * Endpoint:
 * - POST /api/auth/register (Khusus Pengguna Saja)
 * - POST /api/auth/login    (Pengguna & Admin Bawaan)
 * - GET  /api/auth/me
 */

const express = require('express');
const cors = require('cors');
const jwt = require('jsonwebtoken');
const bcrypt = require('bcryptjs');
const crypto = require('crypto');

const app = express();
const PORT = process.env.PORT || 5000;
const JWT_SECRET = process.env.JWT_SECRET || 'bengkelin-malang-secret-key-2026';

app.use(cors());
app.use(express.json());

// In-Memory / Database Seed Bawaan (Admin disediakan sejak awal)
const users = [
  {
    id: 'admin-default-malang-01',
    name: 'Administrator Bengkelin Malang',
    email: 'admin@bengkelin.id',
    // Hash untuk password 'admin123'
    password_hash: bcrypt.hashSync('admin123', 10),
    phone_number: '081299998888',
    role: 'ADMIN',
    region: 'Kota Malang',
    is_verified: true,
    created_at: new Date().toISOString(),
    updated_at: new Date().toISOString()
  }
];

// 1. Endpoint Registrasi (Khusus Pengguna Saja - Admin disediakan dari awal)
app.post('/api/auth/register', async (req, res) => {
  try {
    const { name, email, password, phone_number, region } = req.body;

    if (!name || !email || !password || !phone_number || !region) {
      return res.status(400).json({ success: false, message: 'Semua kolom wajib diisi.' });
    }

    // Larangan pendaftaran sebagai admin
    if (email.toLowerCase().includes('admin') || req.body.role === 'ADMIN') {
      return res.status(403).json({
        success: false,
        message: 'Pendaftaran Administrator dinonaktifkan. Gunakan akun Admin bawaan: admin@bengkelin.id / admin123'
      });
    }

    const existingUser = users.find(u => u.email.toLowerCase() === email.toLowerCase());
    if (existingUser) {
      return res.status(400).json({ success: false, message: 'Email sudah terdaftar. Silakan login.' });
    }

    const passwordHash = await bcrypt.hash(password, 10);
    const userId = crypto.randomUUID();

    // Pendaftaran selalu ditetapkan sebagai role PENGGUNA
    const newUser = {
      id: userId,
      name,
      email: email.toLowerCase(),
      password_hash: passwordHash,
      phone_number,
      role: 'PENGGUNA',
      region,
      is_verified: true,
      created_at: new Date().toISOString(),
      updated_at: new Date().toISOString()
    };

    users.push(newUser);

    // Buat JWT Token
    const token = jwt.sign({ id: newUser.id, role: newUser.role, email: newUser.email }, JWT_SECRET, { expiresIn: '30d' });

    const { password_hash, ...userProfile } = newUser;
    return res.status(201).json({
      success: true,
      message: 'Pendaftaran Pengguna berhasil. Selamat datang di Bengkelin Malang Raya!',
      token,
      user: userProfile
    });
  } catch (error) {
    return res.status(500).json({ success: false, message: 'Internal server error: ' + error.message });
  }
});

// 2. Endpoint Login
app.post('/api/auth/login', async (req, res) => {
  try {
    const { email, password } = req.body;

    const user = users.find(u => u.email.toLowerCase() === (email || '').toLowerCase());
    if (!user) {
      return res.status(401).json({ success: false, message: 'Email atau kata sandi salah.' });
    }

    const isMatch = await bcrypt.compare(password, user.password_hash);
    if (!isMatch) {
      return res.status(401).json({ success: false, message: 'Email atau kata sandi salah.' });
    }

    const token = jwt.sign({ id: user.id, role: user.role, email: user.email }, JWT_SECRET, { expiresIn: '30d' });
    const { password_hash, ...userProfile } = user;

    return res.json({
      success: true,
      message: 'Login berhasil.',
      token,
      user: userProfile
    });
  } catch (error) {
    return res.status(500).json({ success: false, message: 'Internal server error: ' + error.message });
  }
});

// 3. Endpoint Get Profile
app.get('/api/auth/me', (req, res) => {
  const authHeader = req.headers['authorization'];
  const token = authHeader && authHeader.split(' ')[1];
  if (!token) return res.status(401).json({ success: false, message: 'Token otentikasi tidak ditemukan.' });

  jwt.verify(token, JWT_SECRET, (err, decoded) => {
    if (err) return res.status(403).json({ success: false, message: 'Token tidak valid atau kadaluarsa.' });
    const user = users.find(u => u.id === decoded.id);
    if (!user) return res.status(404).json({ success: false, message: 'Pengguna tidak ditemukan.' });

    const { password_hash, ...userProfile } = user;
    return res.json(userProfile);
  });
});

// 4. Endpoint Ambil Daftar Bengkel Google Maps Malang Raya
const googleMapsWorkshops = [
  {
    id: "w-1",
    name: "Bengkel Mobil BOS Suhat Malang",
    category: "MOBIL",
    region: "Kota Malang",
    address: "Jl. Soekarno Hatta No. 45, Lowokwaru, Kota Malang",
    latitude: -7.9429,
    longitude: 112.6163,
    distance_km: 0.8,
    rating: 4.8,
    reviews_count: 342,
    is_open: true,
    opening_hours: "08:00 - 17:00 WIB",
    phone_number: "081234567891",
    specialties: ["Ganti Oli", "Tune Up", "Spooring Balancing", "AC Mobil"],
    google_maps_url: "https://www.google.com/maps/search/?api=1&query=Bengkel+Mobil+BOS+Suhat+Malang"
  },
  {
    id: "w-2",
    name: "AHASS 0001 Kartika Sari Motor Malang",
    category: "MOTOR",
    region: "Kota Malang",
    address: "Jl. Jaksa Agung Suprapto No. 54, Klojen, Kota Malang",
    latitude: -7.9733,
    longitude: 112.6315,
    distance_km: 1.2,
    rating: 4.7,
    reviews_count: 520,
    is_open: true,
    opening_hours: "07:30 - 16:30 WIB",
    phone_number: "081298765432",
    specialties: ["Servis Injeksi", "Ganti Oli Honda", "Sparepart Asli HGP"],
    google_maps_url: "https://www.google.com/maps/search/?api=1&query=AHASS+Kartika+Sari+Motor+Klojen+Malang"
  },
  {
    id: "w-3",
    name: "Bengkel Motor Yamaha Sentral Malang",
    category: "MOTOR",
    region: "Kota Malang",
    address: "Jl. Basuki Rahmat No. 35, Klojen, Kota Malang",
    latitude: -7.9782,
    longitude: 112.6288,
    distance_km: 1.6,
    rating: 4.6,
    reviews_count: 295,
    is_open: true,
    opening_hours: "08:00 - 17:00 WIB",
    phone_number: "081333444555",
    specialties: ["Servis Yamaha", "CVT Matic", "Rem & Ban"],
    google_maps_url: "https://www.google.com/maps/search/?api=1&query=Yamaha+Sentral+Basuki+Rahmat+Malang"
  },
  {
    id: "w-4",
    name: "Bengkel Mobil Auto2000 Sukun Malang",
    category: "MOBIL",
    region: "Kota Malang",
    address: "Jl. S. Supriadi No. 35, Sukun, Kota Malang",
    latitude: -7.9945,
    longitude: 112.6212,
    distance_km: 2.5,
    rating: 4.9,
    reviews_count: 810,
    is_open: true,
    opening_hours: "08:00 - 16:00 WIB",
    phone_number: "081244556677",
    specialties: ["Servis Berkala Toyota", "Body & Paint", "Mesin & Transmisi"],
    google_maps_url: "https://www.google.com/maps/search/?api=1&query=Auto2000+Sukun+Malang"
  },
  {
    id: "w-5",
    name: "Bengkel Mobil Dokter Mobil Singosari",
    category: "MOBIL",
    region: "Kabupaten Malang",
    address: "Jl. Raya Singosari No. 188, Singosari, Kab. Malang",
    latitude: -7.8925,
    longitude: 112.6651,
    distance_km: 6.2,
    rating: 4.8,
    reviews_count: 412,
    is_open: true,
    opening_hours: "09:00 - 18:00 WIB",
    phone_number: "081288990011",
    specialties: ["Kuras Oli Matic", "AC Mobil Tanpa Bongkar", "Tune Up Jet Clean"],
    google_maps_url: "https://www.google.com/maps/search/?api=1&query=Dokter+Mobil+Singosari+Kabupaten+Malang"
  },
  {
    id: "w-6",
    name: "Bengkel Motor Jaya Abadi Kepanjen",
    category: "MOTOR",
    region: "Kabupaten Malang",
    address: "Jl. Sultan Agung No. 72, Kepanjen, Kab. Malang",
    latitude: -8.1287,
    longitude: 112.5714,
    distance_km: 14.5,
    rating: 4.6,
    reviews_count: 192,
    is_open: true,
    opening_hours: "08:00 - 17:00 WIB",
    phone_number: "081377889900",
    specialties: ["Servis Umum Motor", "Ganti Ban", "Pres Velg & Rangka"],
    google_maps_url: "https://www.google.com/maps/search/?api=1&query=Bengkel+Motor+Kepanjen+Malang"
  },
  {
    id: "w-7",
    name: "Bengkel Mobil & AC Batu Sejahtera",
    category: "MOBIL",
    region: "Kota Batu",
    address: "Jl. Diponegoro No. 88, Sisir, Kota Batu",
    latitude: -7.8715,
    longitude: 112.5284,
    distance_km: 12.0,
    rating: 4.7,
    reviews_count: 238,
    is_open: true,
    opening_hours: "08:00 - 17:00 WIB",
    phone_number: "081266778899",
    specialties: ["Servis Kaki-kaki Tanjakan Batu", "AC Mobil", "Rem & Kampas"],
    google_maps_url: "https://www.google.com/maps/search/?api=1&query=Bengkel+Mobil+AC+Diponegoro+Kota+Batu"
  },
  {
    id: "w-8",
    name: "Bengkel Motor AHASS Batu Indah",
    category: "MOTOR",
    region: "Kota Batu",
    address: "Jl. Panglima Sudirman No. 32, Ngaglik, Kota Batu",
    latitude: -7.8732,
    longitude: 112.5251,
    distance_km: 13.2,
    rating: 4.8,
    reviews_count: 382,
    is_open: true,
    opening_hours: "07:30 - 16:30 WIB",
    phone_number: "081233221100",
    specialties: ["Servis Motor Wisata Batu", "Ganti Oli", "Tune Up"],
    google_maps_url: "https://www.google.com/maps/search/?api=1&query=AHASS+Panglima+Sudirman+Kota+Batu"
  }
];

app.get('/api/workshops', (req, res) => {
  const { category, region, query } = req.query;
  let results = [...googleMapsWorkshops];

  if (category && category !== 'SEMUA') {
    results = results.filter(w => w.category === category);
  }
  if (region && region !== 'SEMUA') {
    results = results.filter(w => w.region === region);
  }
  if (query) {
    const q = query.toLowerCase();
    results = results.filter(w =>
      w.name.toLowerCase().includes(q) ||
      w.address.toLowerCase().includes(q) ||
      w.specialties.some(s => s.toLowerCase().includes(q))
    );
  }

  res.json({
    success: true,
    total: results.length,
    workshops: results
  });
});

app.listen(PORT, () => {
  console.log(`Bengkelin REST API Server berjalan di http://localhost:${PORT}`);
});
