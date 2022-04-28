package br.senai.sp.cfp138.restaguide.util;

import java.io.IOException;
import java.util.UUID;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

// auxilia algum processo(é utilizada para quando é necessário que o spring reconheça uma classe)
@Service
public class FirebaseUtil {
	// variável para guardar as credenciais do Firebase
	private Credentials credenciais;
	// variável para acessar o storage
	private Storage storage;
	// constante para o nome do bucket
	private final String BUCKET_NAME = "restaguide.appspot.com";
	// constante para o prefixo da URL
	private final String PREFIX = "https://firebasestorage.googleapis.com/v0/b/"+BUCKET_NAME+"/o/";
	// constante para sufixo da URL
	private final String SUFFIX = "?alt=media";
	// constante para a URL
	private final String DOWNLOAD_URL = PREFIX + "%s" + SUFFIX;
	
	public FirebaseUtil() {
		// buscar as credenciais (arquivo JSON)
		Resource resource = new ClassPathResource("chavefirebase.json");
		try {
			// ler o arquivo para obter as credenciais
			credenciais = GoogleCredentials.fromStream(resource.getInputStream());
			// acessa o serviço de storage
			// variável storage gera o upload do arquivo
			storage = StorageOptions.newBuilder().setCredentials(credenciais).build().getService();
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	
	// método que faz o upload do arquivo
	public String uploadFile(MultipartFile arquivo) throws IOException {
		// gera uma string aleatória para o nome do arquivo
		String nomeArquivo = UUID.randomUUID().toString() + getExtensao(arquivo.getOriginalFilename());
		// criar um Blobid(Blob é um objeto binário. 
		//Blobs geralmente são objetos de imagem, áudio ou outro objetos multimedia, 
		//apesar de algumas vezes código binário executável ser armazenado como um blob)
		BlobId blobId = BlobId.of(BUCKET_NAME, nomeArquivo);
		// criar um BlobInfo a partir do BlobId
		BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("media").build();
		// manda o BlobInfo para o Storage passando os bytes do arquivo pra ele
		storage.create(blobInfo, arquivo.getBytes());
		// retornar a URL para acessar o arquivo 
		return String.format(DOWNLOAD_URL, nomeArquivo);
	}
	
	// retorna a extensão de um arquivo através do seu nome
	private String getExtensao(String nomeArquivo) {
		// retorna o trecho da string que vai do último ponto até o fim
		return nomeArquivo.substring(nomeArquivo.lastIndexOf('.'));
	}
	
	// método para excluir a foto do Firebase
	public void deletar(String nomeArquivo) {
		// retira o prefixo e o sufixo do arquivo
		nomeArquivo = nomeArquivo.replace(PREFIX, "").replace(SUFFIX, "");
		//pega um Blob através do nome do arquivo
		Blob blob = storage.get(BlobId.of(BUCKET_NAME, nomeArquivo));
		// deleta o arquivo
		storage.delete(blob.getBlobId());
	}
}
