import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.securecache.JumbleFunction.ByteInversionJumble;
import com.securecache.JumbleFunction.CaesarByteJumble;
import com.securecache.JumbleFunction.CircularRotationJumble;
import com.securecache.JumbleFunction.EvenOddJumble;
import com.securecache.JumbleFunction.InterleaveJumble;
import com.securecache.JumbleFunction.NibbleSwapJumble;
import com.securecache.JumbleFunction.PairSwapJumble;
import com.securecache.JumbleFunction.StringLogic;
import com.securecache.JumbleFunction.Stringmagic;
import com.securecache.JumbleFunction.Stringtrick;
import com.securecache.JumbleFunction.TriBlockJumble;
import com.securecache.JumbleFunction.XorMaskJumble;
import com.securecache.secureinterface.JumbleFunctionInterface;

/**
 * Verifies every jumble function satisfies:
 *   (1) roundtrip: reassemble(jumbleData(x)) == x
 *   (2) transformation: jumbleData(x) != x  (for non-trivial inputs)
 *   (3) null/edge-case safety
 */
class JumbleFunctionRoundtripTest {

    // ─── shared payload sizes ───────────────────────────────────────────────
    private static final byte[] EVEN    = {1, 2, 3, 4, 5, 6, 7, 8};
    private static final byte[] ODD     = {10, 20, 30, 40, 50};
    private static final byte[] SINGLE  = {99};
    private static final byte[] TWO     = {7, 13};
    private static final byte[] LARGE;

    static {
        LARGE = new byte[256];
        for (int i = 0; i < LARGE.length; i++) LARGE[i] = (byte) i;
    }

    // ─── factory helper ─────────────────────────────────────────────────────
    static JumbleFunctionInterface[] allFunctions() {
        return new JumbleFunctionInterface[]{
            new Stringmagic(),
            new StringLogic(),
            new Stringtrick(),
            new CircularRotationJumble(),
            new EvenOddJumble(),
            new XorMaskJumble(),
            new NibbleSwapJumble(),
            new ByteInversionJumble(),
            new PairSwapJumble(),
            new TriBlockJumble(),
            new InterleaveJumble(),
            new CaesarByteJumble()
        };
    }

    // ─── roundtrip: even-length array ───────────────────────────────────────
    @Test void stringmagic_roundtrip_even()          { assertRoundtrip(new Stringmagic(),            EVEN); }
    @Test void stringLogic_roundtrip_even()          { assertRoundtrip(new StringLogic(),            EVEN); }
    @Test void stringtrick_roundtrip_even()          { assertRoundtrip(new Stringtrick(),            EVEN); }
    @Test void circularRotation_roundtrip_even()     { assertRoundtrip(new CircularRotationJumble(), EVEN); }
    @Test void evenOdd_roundtrip_even()              { assertRoundtrip(new EvenOddJumble(),          EVEN); }
    @Test void xorMask_roundtrip_even()              { assertRoundtrip(new XorMaskJumble(),          EVEN); }
    @Test void nibbleSwap_roundtrip_even()           { assertRoundtrip(new NibbleSwapJumble(),       EVEN); }
    @Test void byteInversion_roundtrip_even()        { assertRoundtrip(new ByteInversionJumble(),    EVEN); }
    @Test void pairSwap_roundtrip_even()             { assertRoundtrip(new PairSwapJumble(),         EVEN); }
    @Test void triBlock_roundtrip_even()             { assertRoundtrip(new TriBlockJumble(),         EVEN); }
    @Test void interleave_roundtrip_even()           { assertRoundtrip(new InterleaveJumble(),       EVEN); }
    @Test void caesarByte_roundtrip_even()           { assertRoundtrip(new CaesarByteJumble(),       EVEN); }

    // ─── roundtrip: odd-length array ────────────────────────────────────────
    @Test void stringLogic_roundtrip_odd()           { assertRoundtrip(new StringLogic(),            ODD); }
    @Test void circularRotation_roundtrip_odd()      { assertRoundtrip(new CircularRotationJumble(), ODD); }
    @Test void evenOdd_roundtrip_odd()               { assertRoundtrip(new EvenOddJumble(),          ODD); }
    @Test void triBlock_roundtrip_odd()              { assertRoundtrip(new TriBlockJumble(),         ODD); }
    @Test void interleave_roundtrip_odd()            { assertRoundtrip(new InterleaveJumble(),       ODD); }
    @Test void pairSwap_roundtrip_odd()              { assertRoundtrip(new PairSwapJumble(),         ODD); }
    @Test void xorMask_roundtrip_odd()               { assertRoundtrip(new XorMaskJumble(),          ODD); }
    @Test void caesarByte_roundtrip_odd()            { assertRoundtrip(new CaesarByteJumble(),       ODD); }

    // ─── roundtrip: edge sizes ───────────────────────────────────────────────
    @Test void circularRotation_roundtrip_single()   { assertRoundtrip(new CircularRotationJumble(), SINGLE); }
    @Test void stringLogic_roundtrip_two()           { assertRoundtrip(new StringLogic(),            TWO); }
    @Test void interleave_roundtrip_two()            { assertRoundtrip(new InterleaveJumble(),       TWO); }
    @Test void triBlock_roundtrip_two()              { assertRoundtrip(new TriBlockJumble(),         TWO); }

    // ─── roundtrip: large (256-byte) array ──────────────────────────────────
    @Test void stringLogic_roundtrip_large()         { assertRoundtrip(new StringLogic(),            LARGE); }
    @Test void circularRotation_roundtrip_large()    { assertRoundtrip(new CircularRotationJumble(), LARGE); }
    @Test void evenOdd_roundtrip_large()             { assertRoundtrip(new EvenOddJumble(),          LARGE); }
    @Test void xorMask_roundtrip_large()             { assertRoundtrip(new XorMaskJumble(),          LARGE); }
    @Test void nibbleSwap_roundtrip_large()          { assertRoundtrip(new NibbleSwapJumble(),       LARGE); }
    @Test void byteInversion_roundtrip_large()       { assertRoundtrip(new ByteInversionJumble(),    LARGE); }
    @Test void pairSwap_roundtrip_large()            { assertRoundtrip(new PairSwapJumble(),         LARGE); }
    @Test void triBlock_roundtrip_large()            { assertRoundtrip(new TriBlockJumble(),         LARGE); }
    @Test void interleave_roundtrip_large()          { assertRoundtrip(new InterleaveJumble(),       LARGE); }
    @Test void caesarByte_roundtrip_large()          { assertRoundtrip(new CaesarByteJumble(),       LARGE); }

    // ─── transformation sanity: jumbled output must differ from input ────────
    @Test void stringLogic_transforms()              { assertTransforms(new StringLogic(),            EVEN); }
    @Test void stringtrick_transforms()              { assertTransforms(new Stringtrick(),            EVEN); }
    @Test void circularRotation_transforms()         { assertTransforms(new CircularRotationJumble(), EVEN); }
    @Test void evenOdd_transforms()                  { assertTransforms(new EvenOddJumble(),          EVEN); }
    @Test void xorMask_transforms()                  { assertTransforms(new XorMaskJumble(),          EVEN); }
    @Test void nibbleSwap_transforms()               { assertTransforms(new NibbleSwapJumble(),       EVEN); }
    @Test void byteInversion_transforms()            { assertTransforms(new ByteInversionJumble(),    EVEN); }
    @Test void pairSwap_transforms()                 { assertTransforms(new PairSwapJumble(),         EVEN); }
    @Test void triBlock_transforms()                 { assertTransforms(new TriBlockJumble(),         EVEN); }
    @Test void interleave_transforms()               { assertTransforms(new InterleaveJumble(),       EVEN); }
    @Test void caesarByte_transforms()               { assertTransforms(new CaesarByteJumble(),       EVEN); }

    // ─── null safety ────────────────────────────────────────────────────────
    @Test void allFunctions_tolerateNull() {
        for (JumbleFunctionInterface fn : allFunctions()) {
            // Should not throw
            fn.jumbleData(null);
            fn.reassemble(null);
        }
    }

    // ─── chained multi-stage roundtrip ──────────────────────────────────────
    @Test void allFunctions_chainedRoundtrip() {
        byte[] original = "chained-all-functions".getBytes();
        byte[] data = original.clone();

        JumbleFunctionInterface[] fns = allFunctions();

        // apply all in order
        int[] applied = new int[fns.length];
        for (int i = 0; i < fns.length; i++) {
            applied[i] = i;
            data = fns[i].jumbleData(data);
        }

        // reverse all
        for (int i = fns.length - 1; i >= 0; i--) {
            data = fns[i].reassemble(data);
        }

        assertArrayEquals(original, data, "Chained roundtrip through all 12 functions should restore original");
    }

    // ─── helpers ────────────────────────────────────────────────────────────
    private static void assertRoundtrip(JumbleFunctionInterface fn, byte[] input) {
        byte[] copy     = input.clone();
        byte[] jumbled  = fn.jumbleData(copy);
        byte[] restored = fn.reassemble(jumbled);
        assertArrayEquals(input, restored,
            fn.getClass().getSimpleName() + ": reassemble(jumbleData(input)) must equal input");
    }

    private static void assertTransforms(JumbleFunctionInterface fn, byte[] input) {
        byte[] jumbled = fn.jumbleData(input.clone());
        assertFalse(Arrays.equals(input, jumbled),
            fn.getClass().getSimpleName() + ": jumbleData should change the byte order/values");
    }
}

