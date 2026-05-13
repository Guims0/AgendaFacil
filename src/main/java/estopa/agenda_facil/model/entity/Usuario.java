package estopa.agenda_facil.model.entity;

import estopa.agenda_facil.model.enums.RoleUsuario;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


@Entity
@Table(name = "tb_usuarios")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
public class Usuario implements UserDetails {

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
   @Override
   public Collection<? extends GrantedAuthority> getAuthorities() {
     
      if (this.role == RoleUsuario.ESTABELECIMENTO) {
         return List.of(new SimpleGrantedAuthority("ROLE_ESTABELECIMENTO"), new SimpleGrantedAuthority("ROLE_CLIENTE"));
      } else {
         return List.of(new SimpleGrantedAuthority("ROLE_CLIENTE"));
      }
   }

   @Override
   public String getPassword() {
      return senha;
   }

   @Override
   public String getUsername() {
      return email;
   }


   @Override
   public boolean isAccountNonExpired() { return true; }

   @Override
   public boolean isAccountNonLocked() { return true; }

   @Override
   public boolean isCredentialsNonExpired() { return true; }

   @Override
   public boolean isEnabled() { return true; }
}