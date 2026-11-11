package com.example.data

object MotivationalQuotes {
    private val quotes = listOf(
        "\"Pendidikan adalah senjata paling mematikan di dunia, karena dengan pendidikan Anda dapat mengubah dunia.\" — Nelson Mandela",
        "\"Bermimpilah setinggi langit. Jika engkau jatuh, engkau akan jatuh di antara bintang-bintang.\" — Ir. Soekarno",
        "\"Investasi terbaik dengan bunga tertinggi adalah investasi pada ilmu pengetahuan.\" — Benjamin Franklin",
        "\"Disiplin hari ini adalah kunci kemerdekaan dan kesuksesanmu di masa depan!\"",
        "\"Setiap soal yang kamu pecahkan membuktikan bahwa fokusmu jauh lebih kuat daripada godaan menunda-nunda!\"",
        "\"Orang hebat tidak lahir dari kemudahan, mereka ditempa oleh tantangan yang berhasil mereka selesaikan.\"",
        "\"Kemenangan sejati adalah saat kamu berhasil menguasai dirimu sendiri sebelum membuka dunia digital.\"",
        "\"Belajar bukan tentang seberapa cepat kamu paham, tapi tentang konsistensi untuk tidak berhenti mencoba.\"",
        "\"Otakmu adalah otot terkuatmu. Semakin sering dilatih dengan kuis, semakin tajam masa depanmu!\"",
        "\"Nikmati waktu bermainmu dengan bangga, karena kamu telah membayarnya dengan kerja keras berpikir!\"",
        "\"Langkah kecil dalam belajar hari ini adalah lompatan besar untuk cita-citamu esok hari!\"",
        "\"Kamu bukan sekadar membuka aplikasi, kamu baru saja membuka potensi terbaik dalam dirimu!\"",
        "\"Fokus yang tajam adalah superpower di era digital. Kamu baru saja membuktikannya!\"",
        "\"Hargai setiap menit waktumu. Gunakan aplikasi ini dengan bijak setelah tantangan hebat ini!\"",
        "\"Juara tidak pernah menyerah saat salah, mereka belajar dan bangkit kembali menjadi lebih tangguh!\""
    )

    fun getRandomQuote(): String {
        return quotes.random()
    }
}
