package ir.niopdc.policy.domain.blacklist;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "INCB_LIST")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class BlackList {

    @Id
    private String cardId;
//    @Column(name = "RELEASE_TIME")
//    private LocalDateTime releasedTime;
//    @Column(name = "ACTIVE_TIME")
//    private LocalDateTime activationTime;
//    @Column(name = "VER")
//    private int version;
//    private int reason;
}
