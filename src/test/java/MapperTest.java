import com.estate.model.dao.OwnerDAO;
import com.estate.model.dao.PropertyDAO;
import com.estate.model.dao.TransactionDAO;
import com.estate.model.dto.OwnerDTO;
import com.estate.model.dto.PropertyDTO;
import com.estate.model.dto.TransactionDTO;
import com.estate.model.mapper.OwnerMapper;
import com.estate.model.mapper.PropertyMapper;
import com.estate.model.mapper.TransactionMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MapperTest {

    static OwnerDAO ownerDAO;
    static PropertyDAO propertyDAO;
    static TransactionDAO transactionDAO;

    @BeforeAll
    public static void setUpMockito(){
        ownerDAO = Mockito.mock(OwnerDAO.class);
        propertyDAO = Mockito.mock(PropertyDAO.class);
        transactionDAO = Mockito.mock(TransactionDAO.class);
        Mockito.lenient().when(ownerDAO.getUserName()).thenReturn("user1");
        Mockito.lenient().when(ownerDAO.getFirstName()).thenReturn("firstName");
        Mockito.lenient().when(ownerDAO.getLastName()).thenReturn("lastName");
        Mockito.lenient().when(ownerDAO.getBalance()).thenReturn(999L);

        Mockito.lenient().when(propertyDAO.getPropertyId()).thenReturn(1);
        Mockito.lenient().when(propertyDAO.getPropertyOwner()).thenReturn(ownerDAO);
        Mockito.lenient().when(propertyDAO.getAddress()).thenReturn("address1");
        Mockito.lenient().when(propertyDAO.getCost()).thenReturn(123L);

        Mockito.lenient().when(transactionDAO.getBuyer()).thenReturn(ownerDAO);
        Mockito.lenient().when(transactionDAO.getSeller()).thenReturn(ownerDAO);
        Mockito.lenient().when(transactionDAO.getProperty()).thenReturn(propertyDAO);
    }

    @Test
    void Test01_ownerDaoShouldBeMappedToDTO(){
        OwnerDTO ownerDTO = OwnerMapper.INSTANCE.ownerDaoToDto(ownerDAO);

        assertTrue(
                ownerDTO.getUserName().equals(ownerDAO.getUserName()) &&
                        ownerDTO.getFirstName().equals(ownerDAO.getFirstName()) &&
                        ownerDTO.getLastName().equals(ownerDAO.getLastName()) &&
                        ownerDTO.getBalance() == ownerDAO.getBalance()
                , "incorrect mapping, OwnerDao isn't the same as OwnerDto");
    }

    @Test
    void Test02_propertyDaoShouldBeMappedToDTO(){
        PropertyDTO propertyDTO = PropertyMapper.INSTANCE.propertyDaoToDto(propertyDAO);

        assertTrue(
                propertyDTO.getPropertyId() == propertyDAO.getPropertyId() &&
                propertyDTO.getPropertyOwner().equals(propertyDAO.getPropertyOwner().getUserName()) &&
                propertyDTO.getAddress().equals(propertyDAO.getAddress()) &&
                propertyDTO.getCost() == propertyDAO.getCost(),
                "incorrect mapping, PropertyDao isn't the same as PropertyDto");
    }

    @Test
    void Test03_ownerDaoShouldBeMappedToDTO(){
        TransactionDTO transactionDTO = TransactionMapper.INSTANCE.transactionDaoToDto(transactionDAO);

        assertTrue(
                transactionDTO.getBuyer().equals(ownerDAO.getUserName()) &&
                transactionDTO.getSeller().equals(ownerDAO.getUserName()) &&
                propertyDAO.getPropertyId() == transactionDTO.getProperty(),
                "incorrect mapping, TransactionDao isn't the same as TransactionDto");
    }

//    @Test
//    void Test03_propertyDtoShouldBeMappedToDao(@Mock PropertyDTO propertyDTO){
//        Mockito.lenient().when(propertyDTO.getPropertyId()).thenReturn(1);
//        Mockito.lenient().when(propertyDTO.getPropertyOwner()).thenReturn(ownerDAO.getUserName());
//        Mockito.lenient().when(propertyDTO.getAddress()).thenReturn("address1");
//        Mockito.lenient().when(propertyDTO.getCost()).thenReturn(123L);
//
//        PropertyDAO propertyDAO = PropertyMapper.INSTANCE.propertyDtoToDao(propertyDTO);
//
//        assertTrue(
//                propertyDTO.getPropertyId() == propertyDAO.getPropertyId() &&
//                        propertyDTO.getPropertyOwner().equals(propertyDAO.getPropertyOwner().getUserName()) &&
//                        propertyDTO.getAddress().equals(propertyDAO.getAddress()) &&
//                        propertyDTO.getCost() == propertyDAO.getCost(),
//                "incorrect mapping, PropertyDao isn't the same as PropertyDto");
//    }
}
