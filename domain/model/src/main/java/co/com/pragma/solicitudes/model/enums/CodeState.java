package co.com.pragma.solicitudes.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum que define los estados posibles de una Application.
 * Se usa para evitar "códigos mágicos" en el código y darles un significado claro.
 */
@Getter
@RequiredArgsConstructor
public enum CodeState {
    PENDIENTE_REVISION(1L),
    RECHAZADA(2L),
    REVISION_MANUAL(3L),
    APROBADA(4L);

    private final Long id;

    // ✅ Método para obtener enum por id
    public static CodeState fromId(Long id) {
        for (CodeState state : values()) {
            if (state.getId().equals(id)) {
                return state;
            }
        }
        throw new IllegalArgumentException("Estado no válido para id: " + id);
    }
}