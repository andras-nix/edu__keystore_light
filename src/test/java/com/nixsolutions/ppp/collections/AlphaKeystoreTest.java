package com.nixsolutions.ppp.collections;

import java.util.Objects;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayNameGeneration(ReplaceUnderscores.class)
class AlphaKeystoreTest {

  private static final String KEY = "AA-00001";
  private Keystore keystore;
  
  @BeforeEach
  void setUp() {
    keystore = getKeystore();
    assertKeystoreIsEmpty();
  }

  Keystore getKeystore() {
    return new AlphaKeystore();
  }

  private void assertKeystoreIsEmpty() {
    assertEquals(0, keystore.size());
  }

  @Test
  void constructor_OnlyNoArgsDeclared() {
    var implementationType = keystore.getClass();

    var constructors = implementationType.getConstructors();

    assertEquals(1, constructors.length);
    assertEquals(0, constructors[0].getParameterCount());
  }

  @Test
  void constructor_NoArgs_InitEmptyKeystore() throws Exception {

    var noArgsConstructor = getKeystore().getClass().getConstructor();

    Keystore instance = noArgsConstructor.newInstance();

    assertEquals(0, instance.size());
  }

  @Test
  void add_NewKey_ReturnTrueAndAddIt() {
    assertNotContainsKey(KEY);

    assertTrue(keystore.add(KEY));

    assertKeystoreContainsOnlyOneKey(KEY);
  }

  void assertContainsKey(String key) {
    assertTrue(containsKey(key));
  }

  void assertNotContainsKey(String key) {
    assertFalse(containsKey(key));
  }

  private boolean containsKey(String key) {
    for (int i = 0; i < keystore.size(); i++) {
      if (Objects.equals(keystore.get(i), key)) {
        return true;
      }
    }
    return false;
  }

  private void assertKeystoreContainsOnlyOneKey(String key) {
    assertContainsKey(key);
    assertEquals(1, keystore.size());
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(strings = {"", " ", "\t", "\n", "\r", "\r\n", "\n\r"})
  void add_BlankOrNullKey_ReturnFalseWithoutAdd(String key) {
    assertKeystoreIsEmpty();
    assertFalse(keystore.add(key));
    assertKeystoreIsEmpty();
  }

  @Test
  void add_NewKeyIfKeystoreIsFull_ReturnFalseWithoutAdd() {
    fillKeystoreWithKeys();
    assertKeystoreIsFull();
    assertNotContainsKey(KEY);

    assertFalse(keystore.add(KEY));

    assertNotContainsKey(KEY);
    assertKeystoreIsFull();
  }

  private void fillKeystoreWithKeys() {
    IntStream.range(0, Keystore.MAX_CAPACITY)
        .mapToObj(i -> String.format("CC-%05d", i))
        .forEach(keystore::add);
  }

  private void assertKeystoreIsFull() {
    assertEquals(Keystore.MAX_CAPACITY, keystore.size());
  }

  @Test
  void clear_ContainsKeys_RemoveAll() {
    fillKeystoreWithKeys();
    assertKeystoreIsFull();

    keystore.clear();
    assertKeystoreIsEmpty();
  }

  @Test
  void clear_EmptyKeystore_NoOperation() {
    assertKeystoreIsEmpty();

    keystore.clear();
    assertKeystoreIsEmpty();
  }

  @Test
  void contains_KeyNotAdded_ReturnFalse() {
    assertKeystoreIsEmpty();
    assertNotContainsKey(KEY);
  }

  @Test
  void contains_KeyAdded_ReturnTrue() {
    keystore.add(KEY);
    assertContainsKey(KEY);
  }

  @Test
  void contains_Null_ReturnFalse() {
    keystore.add(KEY);
    assertNotContainsKey(null);
  }

  @Test
  void get_ValidIndex_ReturnKey() {
    keystore.add(KEY);
    assertEquals(KEY, keystore.get(0));
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 2, 3, 4, 5})
  void get_ValidButEmptyIndex_ReturnNull(int index) {
    keystore.add(KEY);
    assertNull(keystore.get(index));
  }

  @ParameterizedTest
  @ValueSource(ints = {Integer.MIN_VALUE, -1, Keystore.MAX_CAPACITY, Integer.MAX_VALUE})
  void get_InvalidIndex_ReturnNull(int index) {
    keystore.add(KEY);
    assertNull(keystore.get(index));
  }

  @Test
  void size_AfterInit_ReturnsZero() {
    assertKeystoreIsEmpty();
  }

  @Test
  void size_AfterAddition_Increases() {
    assertKeystoreIsEmpty();
    keystore.add(KEY);
    assertEquals(1, keystore.size());
  }
}