package org.ScotlandYard.objects;

import java.util.List;

public interface Player {
  Move notify(int location, List<Move> list);
}
