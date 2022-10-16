import com.example.model.dao.OwnerDAO;
import com.example.model.dto.OwnerDTO;
import com.example.model.mapper.OwnerMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MappersTester {
    @Test
    public void ownerDaoShouldBeMappedToDTO(){
        OwnerDAO owner = new OwnerDAO("usernametest","first","last",999);
        OwnerDTO ownerDTO = OwnerMapper.INSTANCE.ownerDaoToDto(owner);

        Assertions.assertEquals(ownerDTO.getUserName(),owner.getUserName(),"username should be the same");
        Assertions.assertEquals(ownerDTO.getFirstName(),owner.getFirstName(),"first name should be the same");
        Assertions.assertEquals(ownerDTO.getLastName(),owner.getLastName(), "lastname should be the same");
        Assertions.assertEquals(ownerDTO.getBalance(),owner.getBalance(), "balance should be the same");
    }
}
