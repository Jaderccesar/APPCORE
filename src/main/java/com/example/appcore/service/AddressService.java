package com.example.appcore.service;

import com.example.appcore.model.Address;
import com.example.appcore.model.Course;
import com.example.appcore.model.Telephone;
import com.example.appcore.repository.AddressRepository;
import com.example.appcore.repository.TelephoneRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    public List<Address> findAll() {return addressRepository.findAll();}

    public Optional<Address> findById(Long id) {return addressRepository.findById(id);}

    public Address save(Address address) {
        return addressRepository.save(address);
    }

    public Address update(Long id, Address address) {
        Address existing = addressRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Endereço com o id " + id + " não encontrado"));

        existing.setStreet(address.getStreet());
        existing.setCity(address.getCity());
        existing.setState(address.getState());
        existing.setZip(address.getZip());
        existing.setCountry(address.getCountry());
        existing.setComplement(address.getComplement());
        existing.setNumber(address.getNumber());

        return addressRepository.save(existing);
    }

    public void delete(Long id) {
        addressRepository.deleteById(id);
    }

}
