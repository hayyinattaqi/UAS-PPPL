@aduan
Feature: Manajemen Aduan IPAL
  Sebagai admin BPAL PJK, saya dapat melihat, memfilter, mencari, dan mengelola
  aduan pada modul IPAL (/ipal/aduan). Test suite menggunakan Equivalence
  Partitioning (EP) dan Boundary Value Analysis (BVA) sesuai rancangan TC-01 s/d TC-15.

  Background:
    Given Admin sudah login ke aplikasi Simlab-BPJK

  # -----------------------------------------------------------------------
  # TC-01 — Melihat daftar aduan (EP, positive)
  # -----------------------------------------------------------------------
  @ep @positive @TC-01
  Scenario: Admin melihat daftar aduan setelah login
    When Admin membuka halaman daftar aduan
    Then Halaman daftar aduan berhasil ditampilkan

  # -----------------------------------------------------------------------
  # TC-02, TC-03, TC-04 — Filter status (EP)
  # -----------------------------------------------------------------------
  @ep @filter @TC-02 @TC-03 @TC-04
  Scenario Outline: Admin memfilter aduan berdasarkan status
    When Admin membuka halaman daftar aduan
    And Admin memilih filter status "<status>"
    Then Daftar aduan menampilkan hasil untuk status "<status>"
    Examples:
      | status  |
      | masuk   |
      | proses  |
      | selesai |

  # -----------------------------------------------------------------------
  # TC-05 — Cari keyword valid (EP)
  # -----------------------------------------------------------------------
  @ep @search @TC-05
  Scenario: Admin mencari aduan dengan keyword valid
    When Admin membuka halaman daftar aduan
    And Admin mencari aduan dengan keyword "ADU-"
    Then Daftar aduan menampilkan hasil pencarian

  # -----------------------------------------------------------------------
  # TC-06 — Cari keyword kosong (EP)
  # -----------------------------------------------------------------------
  @ep @search @TC-06
  Scenario: Admin mencari dengan keyword kosong menampilkan semua aduan
    When Admin membuka halaman daftar aduan
    And Admin mencari aduan dengan keyword ""
    Then Daftar aduan menampilkan semua aduan

  # -----------------------------------------------------------------------
  # TC-07 — Cari keyword tidak ditemukan (EP)
  # -----------------------------------------------------------------------
  @ep @search @TC-07
  Scenario: Admin mencari keyword yang tidak ada menampilkan kondisi kosong
    When Admin membuka halaman daftar aduan
    And Admin mencari aduan dengan keyword "ZZZZNOTFOUND"
    Then Daftar aduan menampilkan kondisi kosong

  # -----------------------------------------------------------------------
  # TC-08 — Lihat detail aduan (EP)
  # -----------------------------------------------------------------------
  @ep @detail @TC-08
  Scenario: Admin melihat detail aduan
    When Admin membuka halaman daftar aduan
    And Admin membuka detail aduan pertama
    Then Halaman detail aduan berhasil ditampilkan

  # -----------------------------------------------------------------------
  # TC-09 — Terima aduan berstatus masuk (EP)
  # -----------------------------------------------------------------------
  @ep @workflow @TC-09
  Scenario: Admin menerima aduan berstatus masuk
    When Admin membuka halaman daftar aduan
    And Admin membuka detail aduan berstatus "masuk"
    And Admin menekan tombol aksi "Terima"
    Then Status aduan berubah menjadi "proses"

  # -----------------------------------------------------------------------
  # TC-10 — Tolak aduan berstatus masuk (EP)
  # -----------------------------------------------------------------------
  @ep @workflow @TC-10
  Scenario: Admin menolak aduan berstatus masuk
    When Admin membuka halaman daftar aduan
    And Admin membuka detail aduan berstatus "masuk"
    And Admin menekan tombol aksi "Tolak"
    Then Status aduan berubah menjadi "ditolak"

  # -----------------------------------------------------------------------
  # TC-11 — Mulai perbaikan aduan berstatus proses (EP)
  # -----------------------------------------------------------------------
  @ep @workflow @TC-11
  Scenario: Admin memulai perbaikan aduan berstatus proses
    When Admin membuka halaman daftar aduan
    And Admin membuka detail aduan berstatus "proses"
    And Admin menekan tombol aksi "Mulai Perbaikan"
    Then Status aduan berubah menjadi "perbaikan"

  # -----------------------------------------------------------------------
  # TC-12, TC-13, TC-14 — BVA catatan progress
  # -----------------------------------------------------------------------
  @bva @catatan @TC-12 @TC-13 @TC-14
  Scenario Outline: BVA catatan progress sepanjang <panjang> karakter
    When Admin membuka halaman daftar aduan
    And Admin membuka detail aduan berstatus "proses"
    And Admin memasukkan catatan progress sepanjang <panjang> karakter
    And Admin menyimpan catatan progress
    Then Catatan progress <hasil>
    Examples:
      | panjang | hasil             |
      | 1       | berhasil disimpan |
      | 5000    | berhasil disimpan |
      | 5001    | gagal disimpan    |

  # -----------------------------------------------------------------------
  # TC-15 — Tandai selesai aduan berstatus proses (EP)
  # -----------------------------------------------------------------------
  @ep @workflow @TC-15
  Scenario: Admin menandai selesai aduan berstatus proses
    When Admin membuka halaman daftar aduan
    And Admin membuka detail aduan berstatus "proses"
    And Admin menekan tombol aksi "Tandai Selesai"
    Then Status aduan berubah menjadi "selesai"
