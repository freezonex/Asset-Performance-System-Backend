package com.freezonex.aps.modules.asset.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author penglifr
 * @since 2024/05/11 14:32
 */
@Data
public class AssetAttachmentUploadDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String attachmentName;

    private String attachmentDir;
}
