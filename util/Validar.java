package util;

import java.net.URL;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import exceptions.ValidarException;

/**
 * @author Alexandre William
 * 
 * Classe com funções de validação de campos importantes.
 * 
 * As funções são baseadas para validar e retornar o valor de entrada
 * na formatação ideal para um Banco de Dados.
 * 
 * Todas as funções recebem como parâmetro uma String e retornam a mesma com
 * a formatação, e validada caso possua veracidade.
 * 
 * Caso o valor de entrada não seja valido, sera gerado uma Exception e retornado o valor null
 * 
 * As funções de validação de RG e IE são aplicáveis apenas para o Estado de São Paulo
 * devido a possuir diferentes tipos de algoritmos em diferentes Estados.
 * 
 * Possíveis Exceptions:
 * 
 * • NullPointerException
 * • ValidarException
 */
public class Validar {
	
	/**
	 * Função para validar o CEP. 
	 * 
	 * Caso tudo ocorra certo, retornará o CEP. Caso contrário retornará null
	 * 
	 * @param cep
	 * 
	 * @return cep ou null
	 * 
	 * @exception NullPointerException - Caso não sejam fornecidos parâmetros
	 * @exception ValidarException - Caso o campo não seja válido
	 */
	public static String validarCEP(String cep) {
		try {
			
			if (cep == null) {
				throw new NullPointerException("O valor de entrada não pode ser vázio!");
			}
			
			cep = cep.replaceAll("[^0-9]", "");
			
			if (cep.length() != 8) {
				throw new ValidarException("O valor de entrada não é válido!");
			}
			
			URL url = new URL("https://viacep.com.br/ws/%s/json/".formatted(cep));
			
			HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			
			int status = con.getResponseCode();
			
			if (status != 200) {
				throw new RuntimeException("Internal server error, please try again later");
			}
			else {
				
				StringBuffer bf = new StringBuffer();
				
				Scanner scanner = new Scanner(url.openStream());
				
				while (scanner.hasNext()) {
					bf.append(scanner.nextLine() + "\n");
				}
				
				scanner.close();
				
				if (bf.toString().contains("erro")) {
					throw new ValidarException("O CEP inserido não é válido!");
				}
				else {
					return cep;
				}
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Função para validar o CPF. 
	 * 
	 * Caso tudo ocorra certo, retornará o CPF. Caso contrário retornará null
	 * 
	 * @param cpf
	 * 
	 * @return cpf ou null
	 * 
	 * @exception NullPointerException - Caso não sejam fornecidos parâmetros
	 * @exception ValidarException - Caso o campo não seja válido
	 */
	public static String validarCPF(String cpf) {
		try {
			if (cpf == null) {
				throw new ValidarException("O valor de entrada não pode ser vázio!");
			}
			
			cpf = cpf.replaceAll("[^0-9]", "");

			if (cpf.length() != 11) {
				throw new ValidarException("O valor de entrada não é válido!");
			}

			int result1 = 0, result2 = 0;

			for (int i = 0, aux = 10; i < cpf.length() - 2; i++, aux--) {
				result1 += aux * Integer.parseInt(String.valueOf(cpf.charAt(i)));
			}

			for (int i = 0, aux = 11; i < cpf.length() - 1; i++, aux--) {
				result2 += aux * Integer.parseInt(String.valueOf(cpf.charAt(i)));
			}

			result1 = ((result1 * 10) % 11) == 10 ? 0 : ((result1 * 10) % 11);
			result2 = ((result2 * 10) % 11) == 10 ? 0 : ((result2 * 10) % 11);

			if (result1 == Integer.parseInt(String.valueOf(cpf.charAt(9)))
					&& result2 == Integer.parseInt(String.valueOf(cpf.charAt(10))))
				return cpf;
			else
				throw new ValidarException("O CPF inserido não é válido!");
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Função para validar o CNPJ. 
	 * 
	 * Caso tudo ocorra certo, retornará o CNPJ. Caso contrário retornará null
	 * 
	 * @param cnpj
	 * 
	 * @return cnpj ou null
	 * 
	 * @exception NullPointerException - Caso não sejam fornecidos parâmetros
	 * @exception ValidarException - Caso o campo não seja válido
	 */
	public static String validarCNPJ(String cnpj) {
		try {
			if (cnpj == null) {
				throw new ValidarException("O valor de entrada não pode ser vázio!");
			}
			
			cnpj = cnpj.replaceAll("[^0-9]", "");

			if (cnpj.length() != 14) {
				throw new ValidarException("O valor de entrada não é válido!");
			}
			
			int result1 = 0, result2 = 0;
			int[] pesos1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2}, pesos2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
			
			for (int i = 0; i < pesos1.length; i++) {
				result1 += pesos1[i] * Integer.parseInt(String.valueOf(cnpj.charAt(i)));
			}
			
			result1 = (result1 % 11) < 2 ? 0 : (11 - (result1 % 11));
			
			for (int i = 0; i < pesos2.length; i++) {
				result2 += pesos2[i] * Integer.parseInt(String.valueOf(cnpj.charAt(i)));
			}
			
			result2 = (result2 % 11) < 2 ? 0 : (11 - (result2 % 11));
			
			if (result1 == Integer.parseInt(String.valueOf(cnpj.charAt(12))) && result2 == Integer.parseInt(String.valueOf(cnpj.charAt(13))))
				return cnpj;
			else
				throw new ValidarException("O CNPJ inserido não é válido!");
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Função para validar a Inscrição Estadual. 
	 * 
	 * Caso tudo ocorra certo, retornará o IE. Caso contrário retornará null
	 * 
	 * @param ie
	 * 
	 * @return ie ou null
	 * 
	 * @exception NullPointerException - Caso não sejam fornecidos parâmetros
	 * @exception ValidarException - Caso o campo não seja válido
	 */
	public static String  validarIE(String ie) {
		try {
			if (ie == null) {
				throw new ValidarException("O valor de entrada não pode ser vázio!");
			}
			
			ie = ie.replaceAll("[^0-9Pp]", "");

			if (ie.contains("P") || ie.contains("p")) {
				
				if (ie.length() != 14) {
					throw new ValidarException("O valor de entrada não é válido!");
				}
				
				int result = 0;
				int[] pesos = {1, 3, 4, 5, 6, 7, 8, 10};
				
				for (int i = 0; i < pesos.length; i++) {
					result += pesos[i] * Integer.parseInt(String.valueOf(ie.charAt(i+1)));
				}
				
				result = (result % 11) == 10 ? 0 : (result % 11);
				
				if (result == Integer.parseInt(String.valueOf(ie.charAt(9))))
					return null;
				else
					throw new ValidarException("A Inscrição Estadual inserida não é válida!");
			}
			else {
				
				if (ie.length() != 12) {
					throw new ValidarException("O valor de entrada não é válido!");
				}
				
				int result1 = 0, result2 = 0;
				
				int[] pesos1 = {1, 3, 4, 5, 6, 7, 8, 10}, pesos2 = {3, 2, 10, 9, 8, 7, 6, 5, 4, 3, 2};
				
				for (int i = 0; i < pesos1.length; i++) {
					result1 += pesos1[i] * Integer.parseInt(String.valueOf(ie.charAt(i)));
				}
				
				result1 = (result1 % 11) == 10 ? 0 : (result1 % 11);
				
				for (int i = 0; i < pesos2.length; i++) {
					result2 += pesos2[i] * Integer.parseInt(String.valueOf(ie.charAt(i)));
				}
				
				result2 = (result2 % 11) == 10 ? 0 : (result2 % 11);
				
				if (result1 == Integer.parseInt(String.valueOf(ie.charAt(8))) && result2 == Integer.parseInt(String.valueOf(ie.charAt(11))))
					return ie;
				else
					throw new ValidarException("A Inscrição Estadual inserida não é válida!");
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Função para validar o Email. 
	 * 
	 * Caso tudo ocorra certo, retornará o Email. Caso contrário retornará null
	 * 
	 * @param email
	 * 
	 * @return email ou null
	 * 
	 * @exception NullPointerException - Caso não sejam fornecidos parâmetros
	 * @exception ValidarException - Caso o campo não seja válido
	 */
	public static String validarEmail(String email) {
		try {
			if (email == null) {
				throw new NullPointerException("O valor de entrada não pode ser vázio!");
			}
			
			if (emailPatternRegex(email))
				return email;
			else
				throw new ValidarException("O email inserido não é válido!");
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * Função interna para validar o Email utilizando expressões regulares. 
	 * 
	 * Caso tudo ocorra certo, retornará true. Caso contrário retornará false
	 * 
	 * @param email
	 * 
	 * @return true ou false
	 */
	private static boolean emailPatternRegex(String email) {
		return Pattern.compile("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@" 
		        + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$")
			      .matcher(email)
			      .matches();
	}

	/**
	 * Função para verificar força da senha. 
	 * 
	 * Caso tudo ocorra certo, retornará a senha. Caso contrário retornará null
	 * 
	 * @param password
	 * 
	 * @return password ou null
	 * 
	 * @exception NullPointerException - Caso não sejam fornecidos parâmetros
	 * @exception ValidarException - Caso o campo não seja válido
	 */
	public static String passwordStrength(String password) {
		try {
			int points = 0;

			String specialChar = password.replaceAll("[a-zA-z0-9]", "");
			String upperCase = password.replaceAll("[^a-z]", "");
			String lowerCase = password.replaceAll("[^A-Z]", "");
			String number = password.replaceAll("[^0-9]", "");

			if (specialChar.length() == 0 || upperCase.length() == 0 || lowerCase.length() == 0 || number.length() == 0) {
				throw new ValidarException("A senha inserida é fraca!");
			}

			points = (specialChar.length() / specialChar.length()) + (upperCase.length() / upperCase.length())
					+ (lowerCase.length() / lowerCase.length()) + (number.length() / number.length());

			if (points == 4)
				return password;
			else 
				throw new ValidarException("A senha inserida é fraca!");
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Função para validar o RG. 
	 * 
	 * Caso tudo ocorra certo, retornará o RG. Caso contrário retornará null
	 * 
	 * @param rg
	 * 
	 * @return rg ou null
	 * 
	 * @exception NullPointerException - Caso não sejam fornecidos parâmetros
	 * @exception ValidarException - Caso o campo não seja válido
	 */
	public static String validarRG(String rg) {
		try {
			if (rg == null) {
				throw new NullPointerException("O valor de entrada não pode ser vázio!");
			}
			
			rg = rg.replaceAll("[^0-9X]", "");
			
			if (rg.length() != 9) {
				throw new ValidarException("O valor de entrada não é válido!");
			}
			
			int result = 0;
			int[] pesos = {2, 3, 4, 5, 6, 7, 8, 9};
			
			for (int i = 0; i < pesos.length; i++) {
				result += pesos[i] * Integer.parseInt(String.valueOf(rg.charAt(i)));
			}
			
			result = (11 - (result % 11)) == 11 ? 0 : (11 - (result % 11));
			
			if (rg.contains("X")) {
				if (result == 10)
					return rg;
				else
					throw new ValidarException("O RG inserido não é válido!");
			}
			else {
				if (result == Integer.parseInt(String.valueOf(rg.charAt(8))))
					return rg;
				else
					throw new ValidarException("O RG inserido não é válido!");
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
