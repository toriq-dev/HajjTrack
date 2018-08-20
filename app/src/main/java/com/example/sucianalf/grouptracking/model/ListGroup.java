package com.example.sucianalf.grouptracking.model;

public class ListGroup {

    private String groupID, nama, nama_kelas, nim, status_dosen, nip, tlp, email, jabatan, jabatanAatauKelas, nipAtauNim, emailAtauTlpn;
    private int ava;

    public ListGroup(){

    }

    public ListGroup(String nama, int ava) {
        this.nama = nama;

//        this.jabatanAatauKelas = jabatanAatauKelas;
//        this.nipAtauNim = nipAtauNim;
//        this.emailAtauTlpn = emailAtauTlpn;
        this.ava = ava;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getNama_kelas() {
        return nama_kelas;
    }

    public void setNama_kelas(String nama_kelas) {
        this.nama_kelas = nama_kelas;
    }

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public String getStatus_dosen() {
        return status_dosen;
    }

    public void setStatus_dosen(String status_dosen) {
        this.status_dosen = status_dosen;
    }

    public String getNip() {
        return nip;
    }

    public void setNip(String nip) {
        this.nip = nip;
    }

    public String getTlp() {
        return tlp;
    }

    public void setTlp(String tlp) {
        this.tlp = tlp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getJabatan() {
        return jabatan;
    }

    public void setJabatan(String jabatan) {
        this.jabatan = jabatan;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getJabatanAatauKelas() {
        return jabatanAatauKelas;
    }

    public void setJabatanAatauKelas(String jabatanAatauKelas) {
        this.jabatanAatauKelas = jabatanAatauKelas;
    }

    public String getNipAtauNim() {
        return nipAtauNim;
    }

    public void setNipAtauNim(String nipAtauNim) {
        this.nipAtauNim = nipAtauNim;
    }

    public String getEmailAtauTlpn() {
        return emailAtauTlpn;
    }

    public void setEmailAtauTlpn(String emailAtauTlpn) {
        this.emailAtauTlpn = emailAtauTlpn;
    }

    public int getAva() {
        return ava;
    }

    public void setAva(int ava) {
        this.ava = ava;
    }

}
