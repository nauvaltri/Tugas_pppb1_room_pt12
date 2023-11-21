package com.example.tugas12

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.tugas12.database.Note
import com.example.tugas12.database.NoteDao
import com.example.tugas12.database.NoteRoomDatabase
import com.example.tugas12.databinding.ActivityActionBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ActionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityActionBinding
    private lateinit var mNotesDao: NoteDao
    private lateinit var executorService: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActionBinding.inflate(layoutInflater)
        executorService = Executors.newSingleThreadExecutor()
        val db = NoteRoomDatabase.getDatabase(this)
        mNotesDao = db!!.noteDao()!!

        setContentView(binding.root)
        val intent =  intent
        val note = intent.getSerializableExtra("EXT_NOTE") as Note?

        with(binding){
            edtTitle.setText(note?.title)
            edtDesc.setText(note?.description)
            edtDate.setText(note?.date)

            //ketika button add di klik
            btnAdd.setOnClickListener {
                // Menyimpan data ke database atau melakukan tindakan lainnya
                insert(
                    Note(
                        title = edtTitle.text.toString(),
                        description = edtDesc.text.toString(),
                        date = edtDate.text.toString()
                    )
                )

                // Mengosongkan field
                setEmptyField()

                // Menampilkan pesan toast
                Toast.makeText(this@ActionActivity, "Berhasil Menambahkan Data!!!", Toast.LENGTH_SHORT).show()

                // Kembali ke halaman utama (MainActivity)
                val intent = Intent(this@ActionActivity, MainActivity::class.java)
                startActivity(intent)
                finish() // Optional: Menutup aktivitas saat kembali ke halaman utama
            }


            // ketika button updatedi click
            btnUpdate.setOnClickListener(){
                if (note != null) {
                    update(
                        Note(
                            id = note.id,
                            title = edtTitle.text.toString(),
                            description = edtDesc.text.toString(),
                            date = edtDate.text.toString()
                        )
                    )
                    // set edt text jadi kosong
                    setEmptyField()

                    // untuk menyelesaikan halaman update data
                    finish()

                    // membuat toast successfull
                    Toast.makeText(this@ActionActivity, "Berhasil Mengupdate Data!!!", Toast.LENGTH_SHORT).show()
                    // Kembali ke halaman utama (MainActivity)
                    val intent = Intent(this@ActionActivity, MainActivity::class.java)
                    startActivity(intent)

                } else {
                    Toast.makeText(this@ActionActivity, "Tak ada data yang valid!!!", Toast.LENGTH_SHORT).show()
                }
            }

            // ketika tn delete di klik
            btnDelete.setOnClickListener {
                if (note != null) {
                    // Menghapus data
                    delete(note)

                    // Mengosongkan field
                    setEmptyField()

                    // Finish activity
                    finish()

                    // Toast berhasil menghapus data
                    Toast.makeText(this@ActionActivity, "Berhasil Menghapus Data!!!", Toast.LENGTH_SHORT).show()

                    // Kembali ke halaman utama (MainActivity)
                    val intent = Intent(this@ActionActivity, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    // Toast: Tak ada data yang valid
                    Toast.makeText(this@ActionActivity, "Tak ada data yang valid!!!", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }
    private fun setEmptyField(){
        with(binding){
            edtTitle.setText("")
            edtDesc.setText("")
            edtDate.setText("")
        }
    }

    private fun insert(note: Note){
        executorService.execute{mNotesDao.insert(note)}
    }

    private fun update(note:Note){
        executorService.execute{mNotesDao.update(note)}
    }

    private fun delete(note: Note){
        executorService.execute{mNotesDao.delete(note)}
    }
}