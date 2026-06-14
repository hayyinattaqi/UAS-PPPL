Feature: Login BPALPJK

  Background:
    Given User membuka halaman login BPALPJK

  @login @whatsapp @positive
  Scenario: Nomor WhatsApp otomatis diawali 62 saat user mengetik angka selain 8
    When User mengetik nomor WhatsApp "5"
    Then Field nomor WhatsApp menampilkan "625"

  @login @whatsapp @positive
  Scenario: Nomor WhatsApp tetap valid saat user mengetik angka 8
    When User mengetik nomor WhatsApp "8"
    Then Field nomor WhatsApp menampilkan "628"

  @login @whatsapp @negative
  Scenario: User mengirim OTP tanpa mengisi nomor WhatsApp
    When User mengklik tombol Kirim Kode OTP
    Then Sistem menampilkan validasi nomor WhatsApp

  @login @email @positive
  Scenario: User membuka form login email
    When User memilih tab Login dengan Email
    Then Form login email ditampilkan

  @login @email @positive
  Scenario: User berhasil login menggunakan email valid
    When User memilih tab Login dengan Email
    And User mengisi email "admin@gmail.com"
    And User mengisi password "password"
    And User mengklik tombol Login
    Then User berhasil masuk ke dashboard BPALPJK

  @login @email @negative
  Scenario: Login email tanpa mengisi data
    When User memilih tab Login dengan Email
    And User mengklik tombol Login
    Then Sistem menampilkan validasi email dan password

  @login @email @negative
  Scenario Outline: Login email menggunakan kredensial tidak valid
    When User memilih tab Login dengan Email
    And User mengisi email "<email>"
    And User mengisi password "<password>"
    And User mengklik tombol Login
    Then Sistem menampilkan pesan login gagal

    Examples:
      | email           | password |
      | salah@test.com  | password |
      | admin@gmail.com | salah123 |

  @login @email @positive
  Scenario: User membuka halaman lupa kata sandi
    When User memilih tab Login dengan Email
    And User mengklik link Lupa kata sandi
    Then User diarahkan ke halaman lupa kata sandi