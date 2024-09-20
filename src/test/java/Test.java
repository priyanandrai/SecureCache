import com.securecache.Loader.SourcesLoader;
import com.securecache.main.SecureCache;
import com.securecache.main.SecureCache.SecureCacheBuilder;


public class Test {
	
	public static void main(String[] args) {
		System.out.println("***************Secure Caching Encryption Start***********");
		try {

			SourcesLoader<String, byte[]>   loader = new SourcesLoader<String, byte[]>() {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public byte[] loadValue(String k) {
					// TODO Auto-generated method stub
					return (k.toUpperCase() + "ddata").getBytes();
				}

			
			};


			 SecureCache<String, byte[]> cache =  new SecureCache.SecureCacheBuilder<String,byte[]>().Loader(loader).build();

			byte[] IamHere = cache.get("Key1");
			System.out.println("data  " + new String(IamHere));

			IamHere = cache.get("Key1");

			IamHere = cache.get("Key1");

			IamHere = cache.get("Key1");
			cache = new SecureCache<String, byte[]>();
			cache.put("Key2", "i ma getupdate".getBytes());
			IamHere = cache.get("Key2");
			IamHere = cache.get("Key2");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("***************Secure Caching Encryption Start***********");
	}

}
