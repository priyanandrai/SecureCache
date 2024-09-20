import com.securecache.Loader.SourcesLoader;

public class RetriveDataFromSources<K, V> extends SourcesLoader<K,V> {
	@Override
	public V loadValue(K v) {
		// TODO Auto-generated method stub
		return (V) "my value ".getBytes();
	}

}
