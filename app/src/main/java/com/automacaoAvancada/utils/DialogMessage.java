package com.automacaoAvancada.utils;

import android.app.Dialog;
import android.content.Context;
import android.widget.Button;
import android.widget.EditText;

import com.automacaoAvancada.R;
import com.google.android.material.textfield.TextInputEditText;

import org.w3c.dom.Text;

import java.util.Objects;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class DialogMessage {

    private Context mContext;
    private Button btnDescriptografar;
    private Button btnCriptografar;
    private Button btnEnviar;
    private EditText idTextInput;

    public DialogMessage(Context context) {
        mContext = context;
    }

    // Interface para tratar os eventos do diálogo


    // Método estático para criar e mostrar o diálogo
    public void show(String texto) {
        Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.dialog_message);

        EditText idTextInput = dialog.findViewById(R.id.idTextMultiLine);
        Button btnDescriptografar = dialog.findViewById(R.id.btnDescriptografar);
        Button btnCriptografar = dialog.findViewById(R.id.btnCriptografar);
        Button btnEnviar = dialog.findViewById(R.id.btnEnviar);
        idTextInput.setText(texto);

        btnDescriptografar.setOnClickListener(view -> {
            String text = idTextInput.getText().toString();
            idTextInput.setText(CryptUtils.decrypt(text));
        });

        btnCriptografar.setOnClickListener(view -> {
            String text = idTextInput.getText().toString();
            idTextInput.setText(CryptUtils.encrypt(text));
        });

        btnEnviar.setOnClickListener(view -> {
            String text = idTextInput.getText().toString();
            LoadingDialog loadingDialog = new LoadingDialog(mContext);
            loadingDialog.setMessage("Enviando mensagem..");
            loadingDialog.show();


        });

        dialog.show();
    }
}
