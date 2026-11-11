package com.example.data

object DefaultQuestions {
    fun getList(): List<QuestionEntity> = listOf(
        // ================= MATEMATIKA (12 SOAL) =================
        QuestionEntity(
            category = "MATEMATIKA",
            questionText = "Hasil dari 15 × 8 - 45 : 3 adalah...",
            optionA = "105",
            optionB = "115",
            optionC = "120",
            optionD = "95",
            correctOption = "A",
            explanation = "15 × 8 = 120. 45 : 3 = 15. Maka 120 - 15 = 105."
        ),
        QuestionEntity(
            category = "MATEMATIKA",
            questionText = "Sebuah persegi memiliki keliling 48 cm. Luas persegi tersebut adalah...",
            optionA = "144 cm²",
            optionB = "120 cm²",
            optionC = "96 cm²",
            optionD = "196 cm²",
            correctOption = "A",
            explanation = "Sisi persegi = 48 / 4 = 12 cm. Luas = sisi × sisi = 12 × 12 = 144 cm²."
        ),
        QuestionEntity(
            category = "MATEMATIKA",
            questionText = "Nilai x dari persamaan linier 3x + 7 = 28 adalah...",
            optionA = "5",
            optionB = "6",
            optionC = "7",
            optionD = "8",
            correctOption = "C",
            explanation = "3x = 28 - 7 = 21. x = 21 / 3 = 7."
        ),
        QuestionEntity(
            category = "MATEMATIKA",
            questionText = "Berapakah 25% dari 240?",
            optionA = "50",
            optionB = "60",
            optionC = "70",
            optionD = "80",
            correctOption = "B",
            explanation = "25% = 1/4. Maka 240 / 4 = 60."
        ),
        QuestionEntity(
            category = "MATEMATIKA",
            questionText = "FPB (Faktor Persekutuan Terbesar) dari bilangan 24 dan 36 adalah...",
            optionA = "6",
            optionB = "8",
            optionC = "12",
            optionD = "72",
            correctOption = "C",
            explanation = "Faktor 24: 1, 2, 3, 4, 6, 8, 12, 24. Faktor 36: 1, 2, 3, 4, 6, 9, 12, 18, 36. FPB = 12."
        ),
        QuestionEntity(
            category = "MATEMATIKA",
            questionText = "KPK (Kelipatan Persekutuan Terkecil) dari 12 dan 18 adalah...",
            optionA = "36",
            optionB = "48",
            optionC = "54",
            optionD = "72",
            correctOption = "A",
            explanation = "Kelipatan 12: 12, 24, 36, ... Kelipatan 18: 18, 36, ... KPK = 36."
        ),
        QuestionEntity(
            category = "MATEMATIKA",
            questionText = "Sebuah lingkaran mempunyai jari-jari 7 cm. Keliling lingkaran tersebut adalah... (π = 22/7)",
            optionA = "22 cm",
            optionB = "44 cm",
            optionC = "88 cm",
            optionD = "154 cm",
            correctOption = "B",
            explanation = "Keliling = 2 × π × r = 2 × (22/7) × 7 = 44 cm."
        ),
        QuestionEntity(
            category = "MATEMATIKA",
            questionText = "Hasil perhitungan dari (-8) + 12 - (-5) adalah...",
            optionA = "-1",
            optionB = "9",
            optionC = "15",
            optionD = "-15",
            correctOption = "B",
            explanation = "(-8) + 12 = 4. 4 - (-5) = 4 + 5 = 9."
        ),
        QuestionEntity(
            category = "MATEMATIKA",
            questionText = "Sebuah segitiga memiliki alas 10 cm dan tinggi 14 cm. Luas segitiga tersebut adalah...",
            optionA = "70 cm²",
            optionB = "140 cm²",
            optionC = "35 cm²",
            optionD = "80 cm²",
            correctOption = "A",
            explanation = "Luas segitiga = 1/2 × alas × tinggi = 1/2 × 10 × 14 = 70 cm²."
        ),
        QuestionEntity(
            category = "MATEMATIKA",
            questionText = "Bentuk pecahan biasa paling sederhana dari 0,75 adalah...",
            optionA = "1/2",
            optionB = "2/3",
            optionC = "3/4",
            optionD = "4/5",
            correctOption = "C",
            explanation = "0,75 = 75/100 = (75:25) / (100:25) = 3/4."
        ),
        QuestionEntity(
            category = "MATEMATIKA",
            questionText = "Jika sebuah mobil melaju dengan kecepatan rata-rata 60 km/jam selama 2,5 jam, jarak yang ditempuh adalah...",
            optionA = "120 km",
            optionB = "140 km",
            optionC = "150 km",
            optionD = "160 km",
            correctOption = "C",
            explanation = "Jarak = kecepatan × waktu = 60 × 2,5 = 150 km."
        ),
        QuestionEntity(
            category = "MATEMATIKA",
            questionText = "Akar kuadrat dari 625 (√625) adalah...",
            optionA = "15",
            optionB = "25",
            optionC = "35",
            optionD = "45",
            correctOption = "B",
            explanation = "25 × 25 = 625. Maka √625 = 25."
        ),

        // ================= IPA / SAINS (12 SOAL) =================
        QuestionEntity(
            category = "IPA",
            questionText = "Organel sel yang berfungsi sebagai tempat berlangsungnya fotosintesis pada tumbuhan adalah...",
            optionA = "Mitokondria",
            optionB = "Kloroplas",
            optionC = "Ribosom",
            optionD = "Vakuola",
            correctOption = "B",
            explanation = "Kloroplas mengandung pigmen klorofil yang menyerap cahaya matahari untuk fotosintesis."
        ),
        QuestionEntity(
            category = "IPA",
            questionText = "Zat makanan yang berfungsi sebagai sumber energi utama bagi tubuh manusia adalah...",
            optionA = "Protein",
            optionB = "Vitamin",
            optionC = "Karbohidrat",
            optionD = "Mineral",
            correctOption = "C",
            explanation = "Karbohidrat dipecah menjadi glukosa yang merupakan bahan bakar energi utama sel tubuh."
        ),
        QuestionEntity(
            category = "IPA",
            questionText = "Planet terbesar di sistem Tata Surya kita adalah...",
            optionA = "Saturnus",
            optionB = "Bumi",
            optionC = "Yupiter",
            optionD = "Neptunus",
            correctOption = "C",
            explanation = "Yupiter adalah planet raksasa gas terbesar di tata surya dengan diameter lebih dari 142.000 km."
        ),
        QuestionEntity(
            category = "IPA",
            questionText = "Peristiwa perpindahan panas tanpa melalui zat perantara disebut...",
            optionA = "Konduksi",
            optionB = "Konveksi",
            optionC = "Radiasi",
            optionD = "Kondensasi",
            correctOption = "C",
            explanation = "Radiasi adalah perpindahan panas melalui pancaran gelombang elektromagnetik tanpa butuh medium (misal panas matahari ke bumi)."
        ),
        QuestionEntity(
            category = "IPA",
            questionText = "Bagian darah yang berperan utama dalam proses pembekuan darah saat terjadi luka adalah...",
            optionA = "Eritrosit",
            optionB = "Leukosit",
            optionC = "Trombosit",
            optionD = "Plasma darah",
            correctOption = "C",
            explanation = "Trombosit (keping darah) melepaskan enzim trombokinase untuk memicu pembekuan darah."
        ),
        QuestionEntity(
            category = "IPA",
            questionText = "Hukum Newton I berbunyi bahwa setiap benda akan mempertahankan keadaan diam atau bergeraknya jika...",
            optionA = "Gaya geseknya besar",
            optionB = "Resultan gaya yang bekerja sama dengan nol",
            optionC = "Massa benda sangat ringan",
            optionD = "Benda dipanaskan",
            correctOption = "B",
            explanation = "Hukum Newton I (Kelembaman/Inersia): ΣF = 0, benda diam tetap diam, benda bergerak tetap bergerak lurus beraturan."
        ),
        QuestionEntity(
            category = "IPA",
            questionText = "Benda yang tidak dapat menghantarkan panas atau listrik dengan baik disebut...",
            optionA = "Konduktor",
            optionB = "Isolator",
            optionC = "Semikonduktor",
            optionD = "Radiator",
            correctOption = "B",
            explanation = "Isolator seperti kayu, karet, dan plastik menahan aliran arus listrik dan panas."
        ),
        QuestionEntity(
            category = "IPA",
            questionText = "Gas yang dibutuhkan tumbuhan untuk melakukan proses fotosintesis adalah...",
            optionA = "Oksigen (O2)",
            optionB = "Karbondioksida (CO2)",
            optionC = "Nitrogen (N2)",
            optionD = "Helium (He)",
            correctOption = "B",
            explanation = "Tumbuhan menyerap Karbondioksida (CO2) dan air (H2O) dengan bantuan cahaya matahari menghasilkan O2 dan glukosa."
        ),
        QuestionEntity(
            category = "IPA",
            questionText = "Perubahan wujud benda dari padat langsung menjadi gas dinamakan...",
            optionA = "Menguap",
            optionB = "Mencair",
            optionC = "Menyublim",
            optionD = "Mengembun",
            correctOption = "C",
            explanation = "Menyublim adalah perubahan wujud dari padat langsung ke gas, seperti pada kapur barus atau es kering."
        ),
        QuestionEntity(
            category = "IPA",
            questionText = "Satuan internasional (SI) untuk mengukur gaya adalah...",
            optionA = "Joule",
            optionB = "Watt",
            optionC = "Newton",
            optionD = "Pascal",
            correctOption = "C",
            explanation = "Satuan gaya dalam SI adalah Newton (N), dinamai dari fisikawan Sir Isaac Newton."
        ),
        QuestionEntity(
            category = "IPA",
            questionText = "Hubungan timbal balik antara dua makhluk hidup yang saling menguntungkan disebut simbiosis...",
            optionA = "Parasitisme",
            optionB = "Komensalisme",
            optionC = "Mutualisme",
            optionD = "Netralisme",
            correctOption = "C",
            explanation = "Mutualisme menguntungkan kedua belah pihak, seperti lebah dengan bunga."
        ),
        QuestionEntity(
            category = "IPA",
            questionText = "Organ pernapasan pada manusia tempat terjadinya pertukaran gas oksigen dan karbondioksida adalah...",
            optionA = "Trakea",
            optionB = "Bronkus",
            optionC = "Alveolus",
            optionD = "Faring",
            correctOption = "C",
            explanation = "Alveolus adalah kantung udara kecil di ujung bronkiolus yang dikelilingi kapiler darah untuk pertukaran O2 dan CO2."
        ),

        // ================= IPS / SOSIAL (12 SOAL) =================
        QuestionEntity(
            category = "IPS",
            questionText = "Kapan proklamasi kemerdekaan Republik Indonesia dibacakan oleh Ir. Soekarno?",
            optionA = "17 Agustus 1945",
            optionB = "1 Juni 1945",
            optionC = "28 Oktober 1928",
            optionD = "20 Mei 1908",
            correctOption = "A",
            explanation = "Proklamasi dibacakan pada hari Jumat, 17 Agustus 1945 di Jalan Pegangsaan Timur No. 56, Jakarta."
        ),
        QuestionEntity(
            category = "IPS",
            questionText = "Secara geografis, wilayah kepulauan Indonesia diapit oleh dua samudra besar, yaitu...",
            optionA = "Samudra Atlantik dan Pasifik",
            optionB = "Samudra Hindia dan Pasifik",
            optionC = "Samudra Arktik dan Hindia",
            optionD = "Samudra Atlantik dan Hindia",
            correctOption = "B",
            explanation = "Indonesia berada di posisi silang strategis antara Samudra Hindia dan Samudra Pasifik serta benua Asia dan Australia."
        ),
        QuestionEntity(
            category = "IPS",
            questionText = "Candi Buddha terbesar di dunia yang terletak di Magelang, Jawa Tengah adalah...",
            optionA = "Candi Prambanan",
            optionB = "Candi Mendut",
            optionC = "Candi Borobudur",
            optionD = "Candi Sewu",
            correctOption = "C",
            explanation = "Candi Borobudur dibangun pada masa dinasti Syailendra sekitar abad ke-8 dan merupakan candi Buddha terbesar di dunia."
        ),
        QuestionEntity(
            category = "IPS",
            questionText = "Organisasi persatuan bangsa-bangsa di kawasan Asia Tenggara adalah...",
            optionA = "APEC",
            optionB = "NATO",
            optionC = "ASEAN",
            optionD = "OPEC",
            correctOption = "C",
            explanation = "ASEAN (Association of Southeast Asian Nations) didirikan pada tanggal 8 Agustus 1967 di Bangkok."
        ),
        QuestionEntity(
            category = "IPS",
            questionText = "Kegiatan manusia memakai atau menghabiskan nilai guna suatu barang/jasa dinamakan kegiatan...",
            optionA = "Produksi",
            optionB = "Distribusi",
            optionC = "Konsumsi",
            optionD = "Investasi",
            correctOption = "C",
            explanation = "Konsumsi adalah kegiatan memanfaatkan atau menghabiskan kegunaan barang dan jasa untuk memenuhi kebutuhan."
        ),
        QuestionEntity(
            category = "IPS",
            questionText = "Garis khayal pada peta yang membagi bumi menjadi belahan utara dan selatan disebut garis...",
            optionA = "Khatulistiwa (Ekuator)",
            optionB = "Bujur",
            optionC = "Meridian",
            optionD = "Greenwich",
            correctOption = "A",
            explanation = "Garis Khatulistiwa (Ekuator) adalah garis lintang 0 derajat yang membagi bumi utara dan selatan."
        ),
        QuestionEntity(
            category = "IPS",
            questionText = "Bapak Koperasi Indonesia yang juga menjabat sebagai Wakil Presiden RI pertama adalah...",
            optionA = "Mohammad Hatta",
            optionB = "Sutan Sjahrir",
            optionC = "Ki Hajar Dewantara",
            optionD = "Agus Salim",
            correctOption = "A",
            explanation = "Drs. Mohammad Hatta dianugerahi gelar Bapak Koperasi Indonesia atas jasanya memperjuangkan ekonomi kerakyatan."
        ),
        QuestionEntity(
            category = "IPS",
            questionText = "Benua terluas sekaligus memiliki populasi penduduk terbanyak di dunia adalah...",
            optionA = "Benua Afrika",
            optionB = "Benua Amerika",
            optionC = "Benua Asia",
            optionD = "Benua Eropa",
            correctOption = "C",
            explanation = "Benua Asia mencakup sekitar 30% dari total luas daratan bumi dan dihuni oleh lebih dari 4,7 miliar orang."
        ),
        QuestionEntity(
            category = "IPS",
            questionText = "Iklim di Indonesia dipengaruhi oleh letak astronomisnya sehingga memiliki iklim...",
            optionA = "Subtropis",
            optionB = "Tropis",
            optionC = "Sedang",
            optionD = "Kutub",
            correctOption = "B",
            explanation = "Indonesia dilalui garis khatulistiwa di zona 6°LU - 11°LS sehingga beriklim tropis dengan dua musim (hujan dan kemarau)."
        ),
        QuestionEntity(
            category = "IPS",
            questionText = "Pemberlakuan pajak, bea cukai, dan retribusi merupakan sumber pendapatan utama bagi...",
            optionA = "Perusahaan Swasta",
            optionB = "Kas Negara (APBN)",
            optionC = "Lembaga Perbankan",
            optionD = "Organisasi Internasional",
            correctOption = "B",
            explanation = "Pajak adalah kontribusi wajib warga negara yang menjadi sumber pendapatan terbesar APBN untuk pembangunan."
        ),
        QuestionEntity(
            category = "IPS",
            questionText = "Peristiwa Sumpah Pemuda yang menjadi tonggak persatuan pemuda seluruh Indonesia diperingati setiap tanggal...",
            optionA = "2 Mei",
            optionB = "20 Mei",
            optionC = "28 Oktober",
            optionD = "10 November",
            correctOption = "C",
            explanation = "Sumpah Pemuda diikrarkan pada Kongres Pemuda II tanggal 28 Oktober 1928 di Jakarta."
        ),
        QuestionEntity(
            category = "IPS",
            questionText = "Faktor utama yang menyebabkan terjadinya perbedaan musim dan pergantian siang-malam secara berturut-turut adalah...",
            optionA = "Rotasi dan Revolusi Bumi",
            optionB = "Pasang surut air laut",
            optionC = "Gerhana matahari",
            optionD = "Aktivitas gunung berapi",
            correctOption = "A",
            explanation = "Rotasi bumi (berputar pada porosnya) menyebabkan siang-malam, sedangkan revolusi bumi mengelilingi matahari menyebabkan pergantian musim."
        )
    )
}
