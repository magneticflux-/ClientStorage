package org.samo_lego.clientstorage.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.samo_lego.clientstorage.util.PacketLimiter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import static org.samo_lego.clientstorage.ClientStorage.MOD_ID;
import static org.samo_lego.clientstorage.ClientStorage.config;
import static org.slf4j.LoggerFactory.getLogger;

public class Config {

    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .serializeNulls()
            .create();
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir() + "/client_storage.json");
    public PacketLimiter limiter = PacketLimiter.VANILLA;

    public int spigotDelay = 300;
    public int packetThreshold = 4;
    public int vanillaDelay = 10;


    public static Screen createConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("mco.configure.world.settings.title"));

        builder.setSavingRunnable(config::save);

        ConfigCategory mainCategory = builder.getOrCreateCategory(Component.translatable("category.clientstorage.general"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        mainCategory.addEntry(entryBuilder
                .startIntSlider(Component.translatable("settings.clientstorage.spigot_delay"),
                        config.spigotDelay, 30, 600)  // Spigot has minimum delay of 30ms
                .setTooltip(Component.translatable("tooltip.clientstorage.spigot_delay"))
                .setSaveConsumer(integer -> config.spigotDelay = integer)
                .build());


        mainCategory.addEntry(entryBuilder
                .startIntSlider(Component.translatable("settings.clientstorage.packet_threshold"),
                        config.packetThreshold, 0, 4)
                .setTooltip(Component.translatable("tooltip.clientstorage.packet_threshold"))
                .setSaveConsumer(integer -> config.packetThreshold = integer)
                .build());


        mainCategory.addEntry(entryBuilder
                .startIntField(Component.translatable("settings.clientstorage.vanilla_delay"), config.vanillaDelay)
                .setTooltip(Component.translatable("tooltip.clientstorage.vanilla_delay"))
                .setMin(0)
                .setSaveConsumer(integer -> config.vanillaDelay = integer)
                .build());

        return builder.build();
    }

    public static Config load() {
        Config newConfig = null;
        if (CONFIG_FILE.exists()) {
            try (var fileReader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(CONFIG_FILE), StandardCharsets.UTF_8)
            )) {
                newConfig = GSON.fromJson(fileReader, Config.class);
            } catch (IOException e) {
                throw new RuntimeException(MOD_ID + " Problem occurred when trying to load config: ", e);
            }
        }
        if (newConfig == null)
            newConfig = new Config();

        newConfig.save();

        return newConfig;
    }

    private void save() {
        try (var writer = new OutputStreamWriter(new FileOutputStream(CONFIG_FILE), StandardCharsets.UTF_8)) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            getLogger(MOD_ID).error("Problem occurred when saving config: " + e.getMessage());
        }
    }
}
