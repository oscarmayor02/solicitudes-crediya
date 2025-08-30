package co.com.pragma.solicitudes.model.user;
import lombok.*;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;


/**
 * Entidad de dominio que representa a un Usuario del sistema.
 *
 * 🔹 Contiene los atributos principales de un usuario.
 * 🔹 Hace parte del dominio limpio (sin dependencias de infraestructura).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {

    /** Número de identificación del usuario (clave primaria en el dominio). */
    private Long idNumber;

    /** Nombre del usuario. */
    private String name;

    /** Apellido del usuario. */
    private String lastName;

    /** Fecha de nacimiento en formato String (YYYY-MM-DD). */
    private String dateOfBirth;

    /** Dirección de residencia. */
    private String address;

    /** Teléfono de contacto. */
    private String telephone;

    /** Correo electrónico. */
    private String email;

    /** Salario base del usuario. */
    private BigDecimal baseSalary;

    /** Documento de identidad . */
    private String identityDocument;

    /** Rol asociado al usuario (FK a Rol). */
    private BigDecimal idRole;
}
