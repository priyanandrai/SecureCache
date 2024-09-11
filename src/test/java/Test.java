import com.securecache.main.SecureCache;

public class Test {
	
	public static void main(String[] args) {
		System.out.println("***************Secure Caching Encryption Start***********");
		try {
			RetriveDataFromSources<String,  byte[]> dataFromSources = new RetriveDataFromSources<String, byte[]>();
			SecureCache<String, byte[]> cache = new SecureCache<String, byte[]>(dataFromSources);
			
			byte[] IamHere = cache.get("Key1");
			System.out.println("data  " + new String(IamHere));
			
			 IamHere = cache.get("Key1");
			System.out.println("data  " + new String(IamHere));
			
			IamHere = cache.get("Key1");
			System.out.println("data  " + new String(IamHere));
			
			IamHere = cache.get("Key1");
			 cache = new SecureCache<String, byte[]>();
			System.out.println("data  " + new String(IamHere));
			cache.put("Key2", "i ma getupdate".getBytes());
			IamHere = cache.get("Key2");
			System.out.println("sadfsdfd "+ IamHere );
			System.out.println("data  " + new String(IamHere));
			IamHere = cache.get("Key2");
			System.out.println("data  " + new String(IamHere));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("***************Secure Caching Encryption Start***********");
	}

}
