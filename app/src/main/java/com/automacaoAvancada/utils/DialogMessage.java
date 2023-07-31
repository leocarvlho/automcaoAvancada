package com.automacaoAvancada.utils;

import android.app.Dialog;
import android.content.Context;
import android.widget.Button;
import android.widget.EditText;

import com.automacaoAvancada.R;

public class DialogMessage { //cria e exibe um diálogo personalizado com botões para criptografia

    private Context mContext;
    //botões:
    private Button btnDescriptografar;
    private Button btnCriptografar;
    private Button btnEnviar;
    //caixa de texto:
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
