package org.samo_lego.clientstorage.fabric_client.util;

public enum ItemDisplayType {
    /**
     * Merge all same items in the same container.
     */
    MERGE_PER_CONTAINER,

    /**
     * Merge all same items.
     */
    MERGE_ALL,

    /**
     * Show each stack separately.
     */
    SEPARATE_ALL,
}
