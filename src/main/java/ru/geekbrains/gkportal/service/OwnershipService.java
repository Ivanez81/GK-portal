package ru.geekbrains.gkportal.service;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.geekbrains.gkportal.dto.OwnershipRegDTO;
import ru.geekbrains.gkportal.entity.Contact;
import ru.geekbrains.gkportal.entity.Ownership;
import ru.geekbrains.gkportal.entity.OwnershipType;
import ru.geekbrains.gkportal.repository.OwnershipRepository;

import java.util.List;

@Service
public class OwnershipService {

    private static final Logger logger = Logger.getLogger(OwnershipService.class);

    private OwnershipRepository ownershipRepository;
    private OwnershipTypeService ownershipTypeService;
    private FlatsService flatService;
    private HouseService houseService;

    @Autowired
    public void setHouseService(HouseService houseService) {
        this.houseService = houseService;
    }

    @Autowired
    public void setFlatService(FlatsService flatService) {
        this.flatService = flatService;
    }

    @Autowired
    public void setOwnershipTypeService(OwnershipTypeService ownershipTypeService) {
        this.ownershipTypeService = ownershipTypeService;
    }

    @Autowired
    public void setOwnershipRepository(OwnershipRepository ownershipRepository) {
        this.ownershipRepository = ownershipRepository;
    }

    public Ownership findByBuildNumberAndHouseBuildNum(Integer buildNumber, Integer houseBuildNum) {
        return ownershipRepository.findByBuildNumberAndHouseBuildNum(buildNumber, houseBuildNum);
    }

    public boolean checkOwnerships(List<OwnershipRegDTO> ownershipList) {
        boolean res = false;
        for (OwnershipRegDTO ownership : ownershipList) {
            ownership.setHousingNumberError(null);
            ownership.setFlatNumberError(null);
            ownership.setPercentageOfOwnerError(null);
            ownership.setSquareError(null);
            ownership.setHasError(false);

            OwnershipType ownershipType = ownership.getOwnershipType();

            if (ownershipType != null) {
                ownershipType = ownershipTypeService.getOwnershipTypeByID(ownershipType.getUuid());
            }

            if (ownershipType != null) {
                if (ownershipType.isRequedHouseNumber()) {
                    if (ownership.getHousingNumber() == null) {
                        ownership.setHousingNumberError("Требуется указать номер дома!");
                        ownership.setHasError(true);
                    } else {
                        int numFloat = -99999;
                        try {
                            numFloat = Integer.valueOf(ownership.getHousingNumber());
                        } catch (Exception e) {
                            ownership.setHasError(true);
                            ownership.setHousingNumberError("Номер дома должен быть числом!");
                        }

                        if (numFloat != -99999)
                            if (!houseService.isHousingNumIsExist(numFloat, true)) {
                            ownership.setHousingNumberError("Номер дома не существует!");
                            ownership.setHasError(true);
                        }
                    }

                }


                if (ownershipType.isRequedPercentageOwner()) {
                    if (ownership.getPercentageOfOwner() == null) {
                        ownership.setPercentageOfOwnerError("Требуется указать % собственности!");
                        ownership.setHasError(true);
                    } else {
                        int percent = -5;
                        try {
                            percent = Integer.valueOf(ownership.getPercentageOfOwner());

                        } catch (Exception e) {
                            ownership.setHasError(true);
                            ownership.setPercentageOfOwnerError("% должен быть числом от 1 до 100");
                        }
                        if (percent != -5)
                            if ((percent <= 0) || (percent > 100)) {
                            ownership.setPercentageOfOwnerError("% собственности должен быть от 1 до 100");
                            ownership.setHasError(true);
                        }
                    }
                }

                if (ownershipType.isRequedSquare()) {
                    if (ownership.getSquare() == null) {
                        ownership.setSquareError("Требуется указать площадь!");
                        ownership.setHasError(true);
                    } else {
                        ownership.setSquare(ownership.getSquare().replace(",", "."));
                        try {
                            Float.valueOf(ownership.getSquare());
                        } catch (Exception e) {
                            ownership.setSquareError("Укажите корректное значение, в качестве разделителя используйте точку!");
                            ownership.setHasError(true);
                        }

                    }
                }

                if (ownership.getFlatNumber() == null) {
                    ownership.setFlatNumberError("Требуется указать номер объекта!");
                    ownership.setHasError(true);
                }


                if (!ownership.isHasError())
                    switch (ownership.getOwnershipType().getUuid()) {


                        case OwnershipTypeService.FLAT_TYPE_UUID: {
                            int numFlat = -9999;
                            try {
                                numFlat = Integer.valueOf(ownership.getFlatNumber());

                            } catch (Exception e) {
                                ownership.setFlatNumberError("Номер квартиры должен быть числом!");
                                ownership.setHasError(true);
                            }

                            if (numFlat != -9999) {
                                if (flatService.getFlatByHouseAndFlatNum(Integer.valueOf(ownership.getHousingNumber()), numFlat, true) == null) {
                                    ownership.setFlatNumberError("Такая квартира не существует");
                                    ownership.setHasError(true);
                                }
                            }
                            break;
                        }

                    }
            } else {
                ownership.setOwnershipTypeError("Не выбран тип недвижимости");
                ownership.setHasError(true);
            }
            if (ownership.isHasError()) res = true;
        }

        return res;
    }


    public Ownership createOrGetOwnership(OwnershipRegDTO ownershipRegDTO, Contact contact, boolean isBuildNumbers) {
        Ownership ownership;
        // заглушка
        if (ownershipRegDTO.getHousingNumber() == null || ownershipRegDTO.getHousingNumber().isEmpty())
            ownershipRegDTO.setHousingNumber("0");

        if (isBuildNumbers)
            ownership = ownershipRepository.findByOwnershipTypeAndContactAndHouseBuildNumAndBuildNumber(ownershipRegDTO.getOwnershipType()
                    , contact, Integer.valueOf(ownershipRegDTO.getHousingNumber()), ownershipRegDTO.getFlatNumber());
        else
            ownership = ownershipRepository.findByOwnershipTypeAndContactAndHouseNumAndNumber(ownershipRegDTO.getOwnershipType()
                    , contact, Integer.valueOf(ownershipRegDTO.getHousingNumber()), ownershipRegDTO.getFlatNumber());

        if (ownership == null) {
            ownership = Ownership.builder().ownershipType(ownershipRegDTO.getOwnershipType()).is_build_num(isBuildNumbers).
                    contact(contact).percentageOfOwner(Integer.valueOf(ownershipRegDTO.getPercentageOfOwner())).square(Float.valueOf(ownershipRegDTO.getSquare())).build();
            if (isBuildNumbers) {
                ownership.setHouseBuildNum(Integer.valueOf(ownershipRegDTO.getHousingNumber()));
                ownership.setBuildNumber(ownershipRegDTO.getFlatNumber());
            } else {
                ownership.setHouseNum(Integer.valueOf(ownershipRegDTO.getHousingNumber()));
                ownership.setNumber(ownershipRegDTO.getFlatNumber());
            }
        }
        return ownership;
    }

//    public Ownership getRealEstate(AnswerResultDTO answerResultDTO) {
//
//    }

}
