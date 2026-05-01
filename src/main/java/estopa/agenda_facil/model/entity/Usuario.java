package estopa.agenda_facil.model.entity;

import estopa.agenda_facil.model.enums.RoleUsuario;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "tb_usuarios")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
public class Usuario {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   private String nome;

   @Column(unique = true)
   private String email;

   private String senha;

   @Enumerated(EnumType.STRING)
   private RoleUsuario role;

   public Usuario(String nome, String email, String senha, RoleUsuario role) {
      this.nome = nome;
      this.email = email;
      this.senha = senha;
      this.role = role;
   }
}