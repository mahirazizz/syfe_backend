package com.syfe.pfm.dto.response;

public record CategoryResponse(String name, String type, boolean isCustom) {
}
package com.syfe.pfm.dto.response;

public record CategoryResponse(Long id, String name, String type) {
}
