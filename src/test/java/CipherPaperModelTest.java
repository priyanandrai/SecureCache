import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.securecache.JumbleFunction.StringLogic;
import com.securecache.JumbleFunction.Stringmagic;
import com.securecache.JumbleFunction.Stringtrick;
import com.securecache.cipher.Cipher;
import com.securecache.secureinterface.JumbleFunctionInterface;

class CipherPaperModelTest {

    private ArrayList<JumbleFunctionInterface> defaultJumbleFunctions() {
        ArrayList<JumbleFunctionInterface> functions = new ArrayList<JumbleFunctionInterface>();
        functions.add(new Stringmagic());
        functions.add(new StringLogic());
        functions.add(new Stringtrick());
        return functions;
    }

    @Test
    void protectAndRevealShouldRoundTripData() throws Exception {
        Cipher cipher = new Cipher();
        ArrayList<JumbleFunctionInterface> functions = defaultJumbleFunctions();
        byte[] plain = "paper-roundtrip".getBytes(StandardCharsets.UTF_8);

        byte[] blob = cipher.protectData(plain, functions);
        byte[] restored = cipher.revealData(blob, functions);

        assertNotNull(blob);
        assertArrayEquals(plain, restored);
    }

    @Test
    void sameInputShouldProduceDifferentBlobsDueToRuntimeRandomness() throws Exception {
        Cipher cipher = new Cipher();
        ArrayList<JumbleFunctionInterface> functions = defaultJumbleFunctions();
        byte[] plain = "same-input".getBytes(StandardCharsets.UTF_8);

        byte[] blob1 = cipher.protectData(plain, functions);
        byte[] blob2 = cipher.protectData(plain, functions);

        assertFalse(Arrays.equals(blob1, blob2));
    }

    @Test
    void tamperedBlobShouldFailDuringReveal() throws Exception {
        Cipher cipher = new Cipher();
        ArrayList<JumbleFunctionInterface> functions = defaultJumbleFunctions();
        byte[] plain = "tamper-check".getBytes(StandardCharsets.UTF_8);

        byte[] blob = cipher.protectData(plain, functions);
        blob[blob.length - 1] = (byte) (blob[blob.length - 1] ^ 0x01);

        assertThrows(Exception.class, () -> cipher.revealData(blob, functions));
    }

    @Test
    void protectShouldReturnNullForNullValue() throws Exception {
        Cipher cipher = new Cipher();
        assertNull(cipher.protectData(null, defaultJumbleFunctions()));
    }

    @Test
    void protectShouldReturnNullForEmptyJumbleList() throws Exception {
        Cipher cipher = new Cipher();
        assertNull(cipher.protectData("x".getBytes(StandardCharsets.UTF_8), new ArrayList<JumbleFunctionInterface>()));
    }

    @Test
    void revealShouldReturnNullForNullBlob() throws Exception {
        Cipher cipher = new Cipher();
        assertNull(cipher.revealData(null, defaultJumbleFunctions()));
    }

    @Test
    void versionHeaderShouldBePresentAndNonNegative() throws Exception {
        Cipher cipher = new Cipher();
        byte[] blob = cipher.protectData("hdr".getBytes(StandardCharsets.UTF_8), defaultJumbleFunctions());

        int version = blob[0] & 0xFF;
        assertNotEquals(-1, version);
    }
}

