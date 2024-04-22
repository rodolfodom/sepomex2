package codelab.android.sepomex

class Municipio {
    val id: Int;
    val nombre: String;
    val estado: Estado;


    constructor(id: Int, nombre: String, estado: Estado) {
        this.id = id;
        this.nombre = nombre;
        this.estado = estado;
    }
}

