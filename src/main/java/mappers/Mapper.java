package mappers;

import org.jvnet.hk2.annotations.Contract;

@Contract
public interface Mapper<F, T> {

	T map(F from);
}
