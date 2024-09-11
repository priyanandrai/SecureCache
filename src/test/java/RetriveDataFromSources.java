import com.securecache.secureinterface.Sources;

public class RetriveDataFromSources<K, V> extends Sources<K,V> {
	@Override
	public V loadValue(K v) {
		// TODO Auto-generated method stub
		return (V) "my value ".getBytes();
	}

}
