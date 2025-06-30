package com.mintit.incentive.common.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mintit.incentive.user.entity.UserEntity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ConvertUtil {

    private ModelMapper modelMapper;

    private ObjectMapper objectMapper;


    public static enum ConvertType {
        DECRYPT("DECRYPT"), NON("NON"), ENCRYPT("ENCRYPT");

        private final String type;

        ConvertType(String type) {this.type = type;}

        public String type() {
            return this.type;
        }
    }

    @PostConstruct
    public void init() {
        this.modelMapper = new ModelMapper();
        this.modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule())
                                              .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public <T, D> D convert(T source, Class<D> destination) {
        return this.convert(destination, modelMapper.map(source, destination), new ArrayList<String>(), ConvertType.NON);
    }

    public <T, D> D encrypt(T source, Class<D> destination) {
        return this.convert(destination, modelMapper.map(source, destination), new ArrayList<String>(), ConvertType.ENCRYPT);
    }

    public <T, D> D encrypt(T source, Class<D> destination, String... cryptField) {
        return this.convert(destination, modelMapper.map(source, destination), this.convertList(cryptField), ConvertType.ENCRYPT);
    }


    public <T> T encrypt(Class<T> clazz, T source, String... cryptField) {
        return this.convert(clazz, source, this.convertList(cryptField), ConvertType.ENCRYPT);
    }

    public <T, D> D decrypt(T source, Class<D> destination) {
        return this.convert(destination, modelMapper.map(source, destination), new ArrayList<String>(), ConvertType.DECRYPT);
    }

    public <T, D> D decrypt(T source, Class<D> destination, String... cryptField) {
        return this.convert(destination, modelMapper.map(source, destination), this.convertList(cryptField), ConvertType.DECRYPT);
    }

    public <S extends T, T> T decrypt(Class<S> clazz, T source, String... cryptField) {
        return this.convert(clazz, source, this.convertList(cryptField), ConvertType.DECRYPT);
    }

    public <S extends T, T> T convert(Class<S> clazz, T source, ConvertType convertType, String... cryptField) {
        return this.convert(clazz, source, this.convertList(cryptField), convertType);
    }


    public <S extends T, T> T fixedKeys(Class<S> clazz, T source) {
        return this.fixedKeys(clazz, source, "new", "id", "password");
    }

    public <S, T> S fixedKeys(TypeReference<S> valueTypeRef, T source) {
        return this.fixedKeys(valueTypeRef, source, "new", "id", "password");
    }

    public <S, T> S fixedKeys(TypeReference<S> valueTypeRef, T source, String... removeKeys) {
        return this.fixedKeys(valueTypeRef, source, this.convertList(removeKeys));
    }

    public <S extends T, T> T fixedKeys(Class<S> clazz, T source, String... removeKeys) {
        return this.fixedKeys(clazz, source, this.convertList(removeKeys));
    }

    public <S, T> S fixedKeys(TypeReference<S> valueTypeRef, T source, List<String> removeKeys) {
        Map<String, Object> convertMap = objectMapper.convertValue(source, new TypeReference<Map<String, Object>>() {})
                                                     .entrySet()
                                                     .stream()
                                                     .filter(e -> !removeKeys.contains(e.getKey()))
                                                     .collect(Collectors.toMap(Map.Entry::getKey, (e) ->
                                                         e.getValue() == null
                                                         ? ""
                                                         : e.getValue()));

        return objectMapper.convertValue(convertMap, valueTypeRef);
    }

    public <S extends T, T> T fixedKeys(Class<S> clazz, T source, List<String> removeKeys) {
        Map<String, Object> convertMap = objectMapper.convertValue(source, new TypeReference<Map<String, Object>>() {})
                                                     .entrySet()
                                                     .stream()
                                                     .filter(e -> !removeKeys.contains(e.getKey()))
                                                     .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return objectMapper.convertValue(convertMap, clazz);
    }

    public <T> Map<String, Object> toMap(T source) {
        return this.fixedKeys(new TypeReference<Map<String, Object>>() {}, source);
    }

    public <T> Map<String, Object> toMap(T source, String... removeKeys) {
        return this.fixedKeys(new TypeReference<Map<String, Object>>() {}, source, this.convertList(removeKeys));
    }

    public <T> T convertValue(Map<String, Object> map, Class<T> clazz) {
        return objectMapper.convertValue(map, clazz);
    }

    public <S extends T, T> T convert(Class<S> clazz, T source, List<String> cryptField, ConvertType convertType) {
        switch (convertType) {
            case NON:
                return source;
            case DECRYPT:
            case ENCRYPT:
                try {
                    Map<String, Object> decryptMap = objectMapper.convertValue(source, new TypeReference<Map<String, Object>>() {})
                                                                 .entrySet()
                                                                 .stream()
                                                                 .filter(e -> e.getValue() != null)
                                                                 .peek(e -> {
                                                                     if (cryptField.contains(e.getKey())) {
                                                                         try {
                                                                             String value = (String) e.getValue();
                                                                             e.setValue(convertType.type()
                                                                                                   .equals("DECRYPT")
                                                                                        ? CryptUtil.decryptAES256(value)
                                                                                        : CryptUtil.encryptAES256(value));
                                                                         } catch (Exception ex) {
                                                                             log.error("Convert Util Crypt Error", ex);
                                                                         }
                                                                     }
                                                                 })
                                                                 .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                    return objectMapper.convertValue(decryptMap, clazz);
                } catch (Exception ex) {
                    log.error("Convert Util Error", ex);
                    return source;
                }
        }
        return source;
    }

    public LinkedHashMap<String, Object> getQueryParams(Class<?> clazz, Map<String, Object> params, String... keys) {
        List<Map<String, Object>> list = new ArrayList<>();
        Arrays.stream(keys).forEach(key -> {
            if (params.get(key) != null) {
                Map<String, Object> queryMap = new HashMap<>();
                queryMap.put("type", "eq");
                queryMap.put("key", key);
                list.add(queryMap);
            }
        });

        return this.getQueryParams(clazz, params, list);
    }

    public LinkedHashMap<String, Object> getQueryParams(Class<?> clazz, Map<String, Object> params, List<Map<String, Object>> keys) {
        LinkedHashMap<String, Object> queryParams = new LinkedHashMap<>();
        List<String> encrypt = this.isEncrypt(clazz);
        keys.forEach(queryMap -> {
            String key = (String) queryMap.get("key");
            try {
                queryMap.put("value", encrypt.contains(key)
                                      ? CryptUtil.encryptAES256((String) params.get(key))
                                      : params.get(key));
            } catch (Exception e) {
                log.error("Convert Util Crypt Error", e);
            }
            queryParams.put(key, queryMap);

        });
        return queryParams;
    }

    private List<String> isEncrypt(Class<?> clazz) {
        List<String> list = new ArrayList<>();
        if (UserEntity.class.isAssignableFrom(clazz)) {
            list.add("userNm");
            list.add("accountNo");
            list.add("mobileNo");
        }
        return list;
    }

    private List<String> convertList(String... cryptField) {
        return Optional.ofNullable(cryptField)
                       .map(Arrays::asList)
                       .orElseGet(Collections::emptyList);
    }


}
