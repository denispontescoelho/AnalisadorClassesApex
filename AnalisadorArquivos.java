package robô;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

import org.apache.commons.io.FileUtils;

public class AnalisadorArquivos2 {
	public static boolean dentroComentarioBloco = false;
	
	public static void main(String[] args) {
		
        principal();
    }

    public static void principal() {
        String caminhoApex = "\\force-app\\main\\default\\classes";
        String caminhoTodos = "\\force-app\\main\\default";
        String diretorio = "C:\\...\\saida.txt"; //Escreva aqui o caminho do arquivo de saída (lista de dispensa de classes)
       
        String raizPath = selecionarDiretorio("Selecione um diretório Salesforce raíz:");
        caminhoTodos = raizPath + caminhoTodos;
        caminhoApex = raizPath + caminhoApex;

        diretorio = selecionarDiretorioResultado("Selecione um diretório para ser gerado o resultado(Vai gerar um arquivo de texto saida.txt):");

        try {
            List<File> apexs = listarArquivos(caminhoApex);
            List<File> arquivos = listarTodosArquivos(caminhoTodos);

            processarArquivos(apexs, arquivos, diretorio);

            System.out.println("Análise concluída. Resultados salvos em 'saida.txt'");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String selecionarDiretorio(String mensagem) {
        JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        fileChooser.setDialogTitle(mensagem);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int escolha = fileChooser.showOpenDialog(null);

        if (escolha == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().getAbsolutePath();
        } else {
            return "";
        }
    }

    private static String selecionarDiretorioResultado(String mensagem) {
        JFileChooser fileChooserResult = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        fileChooserResult.setDialogTitle(mensagem);
        fileChooserResult.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int escolha2 = fileChooserResult.showOpenDialog(null);

        if (escolha2 == JFileChooser.APPROVE_OPTION) {
            return fileChooserResult.getSelectedFile().getAbsolutePath() + "\\saida.txt";
        } else {
            return "Caminho inválido";
        }
    }

    private static List<File> listarArquivos(String caminho) {
        return new ArrayList<File>(FileUtils.listFiles(new File(caminho), null, false));
    }

    private static List<File> listarTodosArquivos(String caminho) {
        List<File> arquivos = new ArrayList<File>();

        File diretorioFile = new File(caminho);
        File[] folders = diretorioFile.listFiles();

        for (File file : folders) {
            if (file.isDirectory()) {
                adicionarArquivos(arquivos, file);
            }
        }

        System.out.println("Terminou a leitura dos arquivos #########: " + arquivos.size());
        return arquivos;
    }

    private static void adicionarArquivos(List<File> arquivos, File file) {
        String pasta = file.getName();
        System.out.println("Pasta: " + pasta);

        if ("aura".equalsIgnoreCase(pasta) || "lwc".equalsIgnoreCase(pasta)) {
            System.out.println("Pasta 2: " + pasta);
            File diretorioFile2 = new File(file.getAbsolutePath());
            File[] folders2 = diretorioFile2.listFiles();

            for (File fold : folders2) {
                String pasta2 = fold.getName();
                System.out.println("Pasta 2: " + pasta2);
                arquivos.addAll(listarArquivos(fold.getAbsolutePath()));
            }
        } else {
            arquivos.addAll(listarArquivos(file.getAbsolutePath()));
        }
    }

    private static void processarArquivos(List<File> apexs, List<File> arquivos, String diretorio) throws IOException {
        File resultadoArq = new File(diretorio);
       FileWriter arquivoSaida = new FileWriter(resultadoArq);
      
       
       arquivoSaida.write("Classes não referenciadas ou completamente comentadas: " + "\n");
       for (File arquivo : apexs) {
           String nomeArquivo = arquivo.getName();
           if (ignorarArquivo(nomeArquivo, arquivo)) {
               continue;
           }
           if (ignorarInv(arquivo)) {
               continue;
           }

           String nomeExtraido = extrairNomeArquivo(nomeArquivo);
           System.out.println("Arquivo:" + nomeExtraido);
           
           
           List<String> correspondencias = analisarOutrosArquivos(arquivos, nomeExtraido);
            List<String> comentados = analisarOutrosArquivosComentados(arquivo); 
            
                     
           if (correspondencias.isEmpty() || !comentados.isEmpty()) {
               arquivoSaida.write("Arquivo: " + nomeArquivo + "\n");
               continue;
           }
       }

       arquivoSaida.close();
   }

   private static boolean ignorarArquivo(String nomeArquivo, File arquivo) {
       return nomeArquivo.contains(".cls-meta") || nomeArquivo.contains("Test.cls")
               || nomeArquivo.contains("DA.cls") || nomeArquivo.contains("DAI.cls"); //Sufixo DAI e DA são específicos da regra de negócio (aplique aqui seus critérios)
   }
   
   private static boolean ignorarInv(File arquivo) throws IOException {       
       List<String> linhasInv = FileUtils.readLines(arquivo, "UTF-8");
       for(String linhas : linhasInv) {
       	if(linhas.trim().contains("@InvocableMethod")) {
       	return true;
       	}
       }
		return false;
   }

   private static String extrairNomeArquivo(String nomeArquivo) {
       int pontoIndex = nomeArquivo.indexOf('.');
       return nomeArquivo.substring(0, pontoIndex);
   }

   private static List<String> analisarOutrosArquivos(List<File> arquivos, String nomeArquivo) throws IOException {
       List<String> correspondenciasEncontradas = new ArrayList<String>();
       
       
       for (File arquivo : arquivos) {
    	      	   
           if (arquivo != null && !arquivo.getName().equals(nomeArquivo + ".cls")&& !arquivo.getName().contains("Test.cls")) {
        	   try {
        		//Implementação código de recorte de linhas comentadas
        	   String conteudoArquivo = removerComentarios(arquivo);
        	    
        		  if (conteudoArquivo.contains(nomeArquivo + ".") || conteudoArquivo.contains(nomeArquivo + "\"")
                      || conteudoArquivo.contains(nomeArquivo + " ") || conteudoArquivo.contains(nomeArquivo + "(")) {
                           correspondenciasEncontradas.add(arquivo.getName());
                         }
                       
           }catch(IOException e) {
               System.out.println("Erro ao processar o arquivo: " + arquivo.getName());
               e.printStackTrace();
           }
           
       }
       }
      
       if(correspondenciasEncontradas.isEmpty()) {
    	   System.out.println("Arquivo:" + nomeArquivo + " " + "Não referenciada");
       }else {
    	   System.out.println("Arquivo:" + nomeArquivo + " " + "referenciada");
       }
       return correspondenciasEncontradas;
       
       
   }

   private static List<String> analisarOutrosArquivosComentados(File arquivo) throws IOException {
       BufferedReader leitor = new BufferedReader(new FileReader(arquivo));
       boolean dentroComentarioBloco = false;
       boolean encontrouCodigo = false;
       boolean dentroDaClasse = false;
       boolean ultimaLinhaComentada = false;
       List<String> linhasCompletamenteComentadas = new ArrayList<>();
       List<String> classesCompletamenteComentadas = new ArrayList<>();

       String linha;
       while ((linha = leitor.readLine()) != null) {
    	   
           linha = linha.trim();
           
           if(linha.isEmpty()) {
        	   continue;
           }
           
           if(linha.trim().contains("{")) {
               dentroDaClasse = true;
               continue;
           }
           
           if(dentroDaClasse) {
        	// Verifica se a linha começa com /* e não está dentro de um bloco de comentários
               if (linha.startsWith("/*") && !dentroComentarioBloco) {
                   dentroComentarioBloco = true;
                   linhasCompletamenteComentadas.add(linha);
               }
               
               if (linha.contains("/**") && dentroComentarioBloco) {
                   dentroComentarioBloco = false;
               }
               
               // Adiciona a linha ao bloco de comentários da classe se estiver dentro do bloco ou começar com /*
               if (dentroComentarioBloco || linha.startsWith("/*") || !linha.endsWith("*/")) {
                   linhasCompletamenteComentadas.add(linha);
                   
               }
               
               
               // Verifica se a linha termina com */
               if (linha.endsWith("*/") && dentroComentarioBloco) {
            	   linhasCompletamenteComentadas.add(linha);
            	   ultimaLinhaComentada = true;
               }
               
                     
               
               if (!dentroComentarioBloco && !linha.startsWith("//") && !linha.startsWith("/*") && !linha.endsWith("}")) {
                   encontrouCodigo = true;
                   break;
               }        
        	   
           }
                                
       }
       if(encontrouCodigo) {
    	    System.out.println("Arquivo:" + arquivo.getName() + " " + "não completamente comentada");
       }
       if(!encontrouCodigo) {
    	   System.out.println("Arquivo:" + arquivo.getName() + " " + "completamente comentada");
    	   classesCompletamenteComentadas.add(arquivo.getName());
       }
       leitor.close();

       // Retorna a lista de classes completamente comentadas
       return classesCompletamenteComentadas;
   }
   
   private static String removerComentarios(File arquivo) throws IOException {
	  
       StringBuilder codigoSemComentarios = new StringBuilder();
       try(BufferedReader leitor = new BufferedReader(new FileReader(arquivo))){
        	   String linha;
               while ((linha = leitor.readLine()) != null) {
                   linha = removerComentariosLinha(linha);
                   codigoSemComentarios.append(linha).append("\n");
               }
               leitor.close();
           }
                  
       return codigoSemComentarios.toString();
   }

   private static String removerComentariosLinha(String linha) {
	   StringBuilder linhaSemComentarios = new StringBuilder();
	  

       for (int i = 0; i < linha.length(); i++) {
           char c = linha.charAt(i);

           
		if (c == '/' && i < linha.length() - 1) {
               char proximoChar = linha.charAt(i + 1);

               if (proximoChar == '/') {
                   if(!dentroComentarioBloco) {
                   	break;// Comentário de linha encontrado, ignore o restante da linha
                   }
                   
               } else if (proximoChar == '*') {
                   dentroComentarioBloco = true;
                   i++; // Avança para evitar duplicar o próximo caractere
                   continue;
               }
           }

           if (c == '*' && i < linha.length() - 1 && linha.charAt(i + 1) == '/') {
               dentroComentarioBloco = false;
               i++; // Avança para evitar duplicar o próximo caractere
               continue;
           }

           if (!dentroComentarioBloco) {
               linhaSemComentarios.append(c);
           }
       }

       return linhaSemComentarios.toString();
   }

   
}
   
