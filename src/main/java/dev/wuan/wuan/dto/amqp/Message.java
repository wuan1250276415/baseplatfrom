package dev.wuan.wuan.dto.amqp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Message {
  private Long id;
  private String name;
}
