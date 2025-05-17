package org.example.Service;

import org.example.DTO.UserTypeDTO;
import org.example.Mapper.UserTypeMapper;
import org.example.Model.UserType;
import org.example.Repository.UserTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserTypeService {
    @Autowired
    private UserTypeRepository userTypeRepository;
    @Autowired
    private UserTypeMapper userTypeMapper;

    public List<UserTypeDTO> findAll() {
        return userTypeRepository.findAll().stream().map(userTypeMapper::toDTO).collect(Collectors.toList());
    }
}
