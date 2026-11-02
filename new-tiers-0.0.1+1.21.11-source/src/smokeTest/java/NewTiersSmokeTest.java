import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.tiers.TiersClient;
import com.tiers.misc.ModMenuIntegration;
import com.tiers.misc.Mode;
import com.tiers.profile.GameMode;
import com.tiers.profile.Status;
import com.tiers.profile.types.MCTiersProfile;
import com.tiers.profile.types.SubtiersProfile;
import com.tiers.profile.types.SuperProfile;
import com.tiers.textures.Icons;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;

/** Parse saved, real responses from the two public APIs without a game window. */
public final class NewTiersSmokeTest {
    private static int checks;

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
        checks++;
    }

    private static String fixture(String name) throws Exception {
        return Files.readString(Path.of("src/smokeTest/resources", name));
    }

    private static void checkCycling(Mode[] modes) throws Exception {
        Method method = TiersClient.class.getDeclaredMethod("cycleEnum", Enum.class, Enum[].class);
        method.setAccessible(true);
        for (int i = 0; i < modes.length; i++) {
            Mode next = (Mode) method.invoke(null, modes[i], (Object) modes);
            check(next == modes[(i + 1) % modes.length], "Provider gamemode cycling skipped a mode");
        }
    }

    public static void main(String[] args) {
        try {
            MCTiersProfile mctiers = new MCTiersProfile(fixture("mctiers-itzrealme.json"));
            check(mctiers.status == Status.READY, "mctiers.net profile did not parse");
            check(mctiers.gameModes.size() == 8, "MCTiers mode list changed unexpectedly");
            GameMode sword = mctiers.getGameMode(Mode.MCTIERS_SWORD);
            check(sword.displayedTier.getString().equals("HT3"), "Current Sword tier was not preserved");
            check(sword.displayedPeakTier.getString().equals("(HT2)"), "Sword peak tier was not preserved");
            GameMode nethop = mctiers.getGameMode(Mode.MCTIERS_NETH_OP);
            check(nethop.displayedTier.getString().equals("RHT1"), "Retired tier was not preserved");
            check(!nethop.hasPeak, "Null peak fields must fall back to the current tier");

            MCTiersProfile unranked = new MCTiersProfile(fixture("mctiers-unranked.json"));
            check(unranked.status == Status.READY, "Empty rankings should still be a valid profile");
            check(unranked.highest == null, "An unranked player should not receive a tier");
            check(unranked.gameModes.stream().allMatch(mode -> mode.status == Status.NOT_EXISTING), "Unranked modes were not cleared");

            SubtiersProfile subtiers = new SubtiersProfile(fixture("subtiers-itzrealme.json"));
            check(subtiers.status == Status.READY, "Subtiers profile did not parse");
            check(subtiers.getGameMode(Mode.SUBTIERS_MINECART).displayedTier.getString().equals("LT3"), "Subtiers Minecart tier was not preserved");
            SubtiersProfile complete = new SubtiersProfile(fixture("subtiers-all-modes.json"));
            check(complete.gameModes.size() == 12, "Subtiers mode list changed unexpectedly");
            check(complete.gameModes.stream().allMatch(mode -> mode.status == Status.READY), "A Subtiers API gamemode key was lost");

            JsonObject replacement = JsonParser.parseString(fixture("mctiers-itzrealme.json"))
                    .getAsJsonObject().getAsJsonObject("rankings").getAsJsonObject("sword").deepCopy();
            replacement.addProperty("peak_tier", 3);
            replacement.addProperty("peak_pos", 0);
            sword.parseTiers(replacement);
            check(!sword.hasPeak && sword.displayedPeakTier == null, "A refresh retained an obsolete peak tier");
            replacement.remove("peak_tier");
            replacement.remove("peak_pos");
            sword.parseTiers(replacement);
            check(sword.status == Status.READY && !sword.hasPeak, "Omitted optional peak fields did not parse");

            checkCycling(Mode.getMCTiersValues());
            checkCycling(Mode.getSubtiersValues());
            check(Mode.values().length == 20, "An unexpected third provider remains");
            check(Icons.Type.values().length == 1 && Icons.Type.values()[0] == Icons.Type.MCTIERS,
                    "A removed styling preset remains selectable");
            check(new ModMenuIntegration() instanceof ModMenuApi, "Mod Menu integration was lost");
            check(new ModMenuIntegration().getModConfigScreenFactory() != null, "Mod Menu config factory is missing");
            System.out.println("New Tiers: " + checks + " API parsing, mode cycling and integration checks passed.");
            System.exit(0);
        } catch (Throwable failure) {
            failure.printStackTrace();
            System.exit(1);
        }
    }
}
