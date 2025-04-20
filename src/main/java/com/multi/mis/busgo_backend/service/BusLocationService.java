package com.multi.mis.busgo_backend.service;

import com.multi.mis.busgo_backend.model.BusLocation;
import com.multi.mis.busgo_backend.model.LocationAddress;
import com.multi.mis.busgo_backend.repository.BusLocationRepository;
import com.multi.mis.busgo_backend.repository.LocationAddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BusLocationService {
    
    @Autowired
    private BusLocationRepository busLocationRepository;
    
    @Autowired
    private LocationAddressRepository locationAddressRepository;
    
    public List<BusLocation> getAllLocations() {
        return busLocationRepository.findAll();
    }
    
    public Optional<BusLocation> getLocationById(Long id) {
        return busLocationRepository.findById(id);
    }
    
    public BusLocation createLocation(BusLocation location) {
        return busLocationRepository.save(location);
    }
    
    public Optional<BusLocation> updateLocation(Long id, BusLocation updatedLocation) {
        return busLocationRepository.findById(id)
                .map(existingLocation -> {
                    if (updatedLocation.getLocationName() != null) {
                        existingLocation.setLocationName(updatedLocation.getLocationName());
                    }
                    if (updatedLocation.getCity() != null) {
                        existingLocation.setCity(updatedLocation.getCity());
                    }
                    if (updatedLocation.getState() != null) {
                        existingLocation.setState(updatedLocation.getState());
                    }
                    if (updatedLocation.getCountry() != null) {
                        existingLocation.setCountry(updatedLocation.getCountry());
                    }
                    if (updatedLocation.getLocationType() != null) {
                        existingLocation.setLocationType(updatedLocation.getLocationType());
                    }
                    return busLocationRepository.save(existingLocation);
                });
    }
    
    public boolean deleteLocation(Long id) {
        if (busLocationRepository.existsById(id)) {
            busLocationRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public List<BusLocation> findByCity(String city) {
        return busLocationRepository.findByCityContainingIgnoreCase(city);
    }
    
    public List<BusLocation> findByState(String state) {
        return busLocationRepository.findByStateContainingIgnoreCase(state);
    }
    
    public List<BusLocation> findByLocationName(String name) {
        return busLocationRepository.findByLocationNameContainingIgnoreCase(name);
    }
    
    public Optional<LocationAddress> getAddressByLocationId(Long locationId) {
        return locationAddressRepository.findByLocationLocationId(locationId);
    }
    
    @Transactional
    public LocationAddress createOrUpdateLocationAddress(Long locationId, LocationAddress address) {
        return busLocationRepository.findById(locationId)
                .map(location -> {
                    address.setLocation(location);
                    LocationAddress savedAddress = locationAddressRepository.save(address);
                    location.setAddress(savedAddress);
                    busLocationRepository.save(location);
                    return savedAddress;
                })
                .orElseThrow(() -> new RuntimeException("Location not found with id: " + locationId));
    }
} 