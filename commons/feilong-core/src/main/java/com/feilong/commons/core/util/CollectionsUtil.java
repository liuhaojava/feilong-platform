/*
 * Copyright (C) 2008 feilong (venusdrogon@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.feilong.commons.core.util;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.feilong.commons.core.bean.BeanUtilException;
import com.feilong.commons.core.bean.PropertyUtil;
import com.feilong.commons.core.entity.JoinStringEntity;
import com.feilong.commons.core.lang.ObjectUtil;

/**
 * {@link Collection} 工具类,是 {@link Collections} 的扩展和补充.<br>
 * 
 * <h3>{@link <a href="http://stamen.iteye.com/blog/2003458">SET-MAP现代诗一首</a>}</h3>
 * 
 * <blockquote>
 * <p>
 * <ul>
 * <li>天下人都知道Set，Map不能重复</li>
 * <li>80%人知道hashCode,equals是判断重复的法则 </li>
 * <li>40%人知道Set添加重复元素时，旧元素不会被覆盖</li>
 * <li>20%人知道Map添加重复键时，旧键不会被覆盖，而值会覆盖</li>
 * </ul>
 * </p>
 * </blockquote>
 * 
 * @author <a href="mailto:venusdrogon@163.com">金鑫</a>
 * @version 1.0 Sep 2, 2010 8:08:40 PM
 * @since 1.0.0
 * @since jdk1.5
 */
public final class CollectionsUtil{

    /** The Constant log. */
    private static final Logger log = LoggerFactory.getLogger(CollectionsUtil.class);

    /** Don't let anyone instantiate this class. */
    private CollectionsUtil(){
        //AssertionError不是必须的. 但它可以避免不小心在类的内部调用构造器. 保证该类在任何情况下都不会被实例化.
        //see 《Effective Java》 2nd
        throw new AssertionError("No " + getClass().getName() + " instances for you!");
    }

    /**
     * 将集合使用连接符号链接成字符串.
     * 
     * @param <T>
     *            the generic type ,必须实现 {@link Serializable} 接口
     * @param collection
     *            集合, 建议基本类型泛型的结合,因为这个方法是直接循环collection 进行拼接
     * @param joinStringEntity
     *            连接字符串 实体
     * @return 如果 collection isNullOrEmpty,返回null<br>
     *         如果 joinStringEntity 是null,默认使用 {@link JoinStringEntity#DEFAULT_CONNECTOR} 进行连接<br>
     *         都不是null,会循环,拼接joinStringEntity.getConnector()
     */
    // XXX 空字符串不拼接
    public static final <T extends Serializable> String toString(final Collection<T> collection,final JoinStringEntity joinStringEntity){

        if (Validator.isNotNullOrEmpty(collection)){

            String connector = JoinStringEntity.DEFAULT_CONNECTOR;
            if (Validator.isNotNullOrEmpty(joinStringEntity)){
                connector = joinStringEntity.getConnector();
            }

            StringBuilder sb = new StringBuilder();
            int i = 0;
            for (T t : collection){
                sb.append(t);
                // 拼接连接符
                if (i < collection.size() - 1){
                    sb.append(connector);
                }
                i++;
            }
            return sb.toString();
        }
        return null;
    }

    /**
     * 将集合转成枚举.
     * 
     * @param <T>
     *            the generic type
     * @param collection
     *            集合
     * @return Enumeration
     * @see Collections#enumeration(Collection)
     */
    public static final <T> Enumeration<T> toEnumeration(final Collection<T> collection){
        return Collections.enumeration(collection);
    }

    /**
     * 将枚举转成集合.
     * 
     * @param <T>
     *            the generic type
     * @param enumeration
     *            the enumeration
     * @return if Validator.isNullOrEmpty(enumeration), return {@link Collections#emptyList()},该emptyList不可以操作<br>
     *         else return {@link Collections#list(Enumeration)}
     * @see Collections#emptyList()
     * @see Collections#EMPTY_LIST
     * @see Collections#list(Enumeration)
     * @see org.apache.commons.collections.EnumerationUtils#toList(Enumeration)
     * @since 1.0.7
     * @since JDK 1.5
     */
    public static final <T> List<T> toList(final Enumeration<T> enumeration){
        if (Validator.isNullOrEmpty(enumeration)){
            return Collections.emptyList();
        }
        ArrayList<T> list = Collections.list(enumeration);
        return list;
    }

    /**
     * 集合转成数组<br>
     * note:由于没有办法自动获得T 泛型的类型, 所以会取第一个值的类型做数组的类型,故需要确保第一个元素不是null.
     * 
     * @param <T>
     *            the generic type
     * @param collection
     *            collection
     * @return 数组,if Validator.isNullOrEmpty(collection),return null
     * @throws IllegalArgumentException
     *             如果list中第一个元素 isNullOrEmpty
     * @see java.lang.reflect.Array#newInstance(Class, int)
     * @see java.lang.reflect.Array#newInstance(Class, int...)
     * @see java.util.Collection#toArray()
     * @see java.util.Collection#toArray(Object[])
     * @see java.util.List#toArray()
     * @see java.util.List#toArray(Object[])
     * @see java.util.Vector#toArray()
     * @see java.util.Vector#toArray(Object[])
     * @see java.util.LinkedList#toArray()
     * @see java.util.LinkedList#toArray(Object[])
     * @see java.util.ArrayList#toArray()
     * @see java.util.ArrayList#toArray(Object[])
     */
    public static <T> T[] toArray(Collection<T> collection) throws IllegalArgumentException{
        if (Validator.isNullOrEmpty(collection)){
            return null;
        }

        //**********************************************************************
        Iterator<T> iterator = collection.iterator();

        T firstT = iterator.next();
        //list.get(0);
        //TODO 可能有更好的方式
        if (Validator.isNullOrEmpty(firstT)){
            throw new IllegalArgumentException("list's first item can't be null/empty!");
        }
        //**********************************************************************
        Class<?> compontType = firstT.getClass();

        int size = collection.size();

        @SuppressWarnings("unchecked")
        T[] tArray = (T[]) Array.newInstance(compontType, size);

        // 如果采用大家常用的把a的length设为0,就需要反射API来创建一个大小为size的数组,而这对性能有一定的影响.
        // 所以最好的方式就是直接把a的length设为Collection的size从而避免调用反射API来达到一定的性能优化.

        //注意，toArray(new Object[0]) 和 toArray() 在功能上是相同的. 
        return collection.toArray(tArray);
    }

    /**
     * 解析对象集合,使用 {@link com.feilong.commons.core.bean.PropertyUtil#getProperty(Object, String)}取到对象特殊属性,拼成List(ArrayList). <br>
     * 支持属性级联获取,支付获取数组,集合,map,自定义bean等属性
     * 
     * <h3>使用示例:</h3>
     * 
     * <blockquote>
     * 
     * <pre>
     * List&lt;User&gt; testList = new ArrayList&lt;User&gt;();
     * 
     * User user;
     * UserInfo userInfo;
     * 
     * //*******************************************************
     * List&lt;UserAddress&gt; userAddresseList = new ArrayList&lt;UserAddress&gt;();
     * UserAddress userAddress = new UserAddress();
     * userAddress.setAddress(&quot;中南海&quot;);
     * userAddresseList.add(userAddress);
     * 
     * //*******************************************************
     * Map&lt;String, String&gt; attrMap = new HashMap&lt;String, String&gt;();
     * attrMap.put(&quot;蜀国&quot;, &quot;赵子龙&quot;);
     * attrMap.put(&quot;魏国&quot;, &quot;张文远&quot;);
     * attrMap.put(&quot;吴国&quot;, &quot;甘兴霸&quot;);
     * 
     * //*******************************************************
     * String[] lovesStrings1 = { &quot;sanguo1&quot;, &quot;xiaoshuo1&quot; };
     * userInfo = new UserInfo();
     * userInfo.setAge(28);
     * 
     * user = new User(2L);
     * user.setLoves(lovesStrings1);
     * user.setUserInfo(userInfo);
     * user.setUserAddresseList(userAddresseList);
     * 
     * user.setAttrMap(attrMap);
     * testList.add(user);
     * 
     * //*****************************************************
     * String[] lovesStrings2 = { &quot;sanguo2&quot;, &quot;xiaoshuo2&quot; };
     * userInfo = new UserInfo();
     * userInfo.setAge(30);
     * 
     * user = new User(3L);
     * user.setLoves(lovesStrings2);
     * user.setUserInfo(userInfo);
     * user.setUserAddresseList(userAddresseList);
     * user.setAttrMap(attrMap);
     * testList.add(user);
     * 
     * //数组
     * List&lt;String&gt; fieldValueList1 = ListUtil.getFieldValueList(testList, &quot;loves[1]&quot;);
     * log.info(JsonUtil.format(fieldValueList1));
     * 
     * //级联对象
     * List&lt;Integer&gt; fieldValueList2 = ListUtil.getFieldValueList(testList, &quot;userInfo.age&quot;);
     * log.info(JsonUtil.format(fieldValueList2));
     * 
     * //Map
     * List&lt;Integer&gt; attrList = ListUtil.getFieldValueList(testList, &quot;attrMap(蜀国)&quot;);
     * log.info(JsonUtil.format(attrList));
     * 
     * //集合
     * List&lt;String&gt; addressList = ListUtil.getFieldValueList(testList, &quot;userAddresseList[0]&quot;);
     * log.info(JsonUtil.format(addressList));
     * </pre>
     * 
     * </blockquote>
     * 
     * @param <T>
     *            返回集合类型 generic type
     * @param <O>
     *            可迭代对象类型 generic type
     * @param objectCollection
     *            任何可以迭代的对象
     * @param propertyName
     *            迭代泛型对象的属性名称,Possibly indexed and/or nested name of the property to be extracted
     * @return 解析迭代集合,取到对象特殊属性,拼成List(ArrayList)
     * @throws NullPointerException
     *             if Validator.isNullOrEmpty(objectCollection) or Validator.isNullOrEmpty(propertyName)
     * @see com.feilong.commons.core.bean.BeanUtil#getProperty(Object, String)
     * @see org.apache.commons.beanutils.PropertyUtils#getProperty(Object, String)
     * @see #getPropertyValueCollection(Collection, String, Collection)
     * @since jdk1.5
     */
    public static <T, O> List<T> getPropertyValueList(Collection<O> objectCollection,String propertyName) throws NullPointerException{
        List<T> list = new ArrayList<T>();
        return getPropertyValueCollection(objectCollection, propertyName, list);
    }

    /**
     * 获得 property value set.
     *
     * @param <T>
     *            the generic type
     * @param <O>
     *            the generic type
     * @param objectCollection
     *            the object collection
     * @param propertyName
     *            the property name
     * @return the property value set
     * @throws NullPointerException
     *             the null pointer exception
     * @see #getPropertyValueCollection(Collection, String, Collection)
     * @since 1.0.8
     */
    public static <T, O> Set<T> getPropertyValueSet(Collection<O> objectCollection,String propertyName) throws NullPointerException{
        Set<T> set = new LinkedHashSet<T>();
        return getPropertyValueCollection(objectCollection, propertyName, set);
    }

    /**
     * 循环objectCollection,调用 {@link PropertyUtil#getProperty(Object, String)} 获得 propertyName的值，塞到 <code>returnCollection</code> 中返回.
     *
     * @param <T>
     *            the generic type
     * @param <O>
     *            the generic type
     * @param <K>
     *            the key type
     * @param objectCollection
     *            the object collection
     * @param propertyName
     *            the property name
     * @param returnCollection
     *            the return collection
     * @return the property value collection
     * @throws NullPointerException
     *             if Validator.isNullOrEmpty(objectCollection) or Validator.isNullOrEmpty(propertyName) or (null == returnCollection)
     * @see com.feilong.commons.core.bean.PropertyUtil#getProperty(Object, String)
     * @since 1.0.8
     */
    private static <T, O, K extends Collection<T>> K getPropertyValueCollection(
                    Collection<O> objectCollection,
                    String propertyName,
                    K returnCollection) throws NullPointerException{
        if (Validator.isNullOrEmpty(objectCollection)){
            throw new NullPointerException("objectCollection is null or empty!");
        }

        if (Validator.isNullOrEmpty(propertyName)){
            throw new NullPointerException("propertyName is null or empty!");
        }

        if (null == returnCollection){
            throw new NullPointerException("returnCollection is null!");
        }

        try{
            for (O bean : objectCollection){
                @SuppressWarnings("unchecked")
                T property = (T) PropertyUtil.getProperty(bean, propertyName);
                returnCollection.add(property);
            }
        }catch (BeanUtilException e){
            log.error(e.getClass().getName(), e);
        }
        return returnCollection;
    }

    /**
     * Finds the first element in the given collection which matches the given predicate.
     * <p>
     * If the input collection or predicate is null, or no element of the collection matches the predicate, null is returned.
     * </p>
     *
     * @param <O>
     *            the generic type
     * @param <V>
     *            the value type
     * @param objectCollection
     *            the object collection
     * @param propertyName
     *            the property name
     * @param value
     *            the value
     * @return the first element of the collection which matches the predicate or null if none could be found
     * @throws NullPointerException
     *             the null pointer exception
     * @see org.apache.commons.collections.CollectionUtils#find(Collection, Predicate)
     */
    @SuppressWarnings("unchecked")
    public static <O, V> O find(Collection<O> objectCollection,String propertyName,V value) throws NullPointerException{
        Predicate predicate = getObjectEqualsPredicate(propertyName, value);
        return (O) org.apache.commons.collections.CollectionUtils.find(objectCollection, predicate);
    }

    /**
     * 循环遍历 <code>objectCollection</code>,返回 当bean propertyName 属性值 equals 特定value 时候的list.
     *
     * @param <O>
     *            the generic type
     * @param <V>
     *            the value type
     * @param objectCollection
     *            the object list
     * @param propertyName
     *            the property name
     * @param value
     *            the value
     * @return the property value list
     * @throws NullPointerException
     *             if Validator.isNullOrEmpty(objectCollection) || Validator.isNullOrEmpty(propertyName)
     * @see org.apache.commons.collections.CollectionUtils#select(Collection, org.apache.commons.collections.Predicate)
     */
    public static <O, V> List<O> select(Collection<O> objectCollection,String propertyName,V value) throws NullPointerException{
        Object[] values = { value };
        return select(objectCollection, propertyName, values);
    }

    /**
     * 调用 {@link PropertyUtil#getProperty(Object, String)} 获得 <code>propertyName</code>的值，判断是否 {@link ArrayUtil#isContain(Object[], Object)}
     * 在 <code>values</code>数组中,如果在，将该对象存入list中返回.
     *
     * @param <O>
     *            the generic type
     * @param <V>
     *            the value type
     * @param objectCollection
     *            the object collection
     * @param propertyName
     *            the property name
     * @param values
     *            the values
     * @return 调用 {@link PropertyUtil#getProperty(Object, String)} 获得 <code>propertyName</code>的值，判断是否
     *         {@link ArrayUtil#isContain(Object[], Object)} 在 <code>values</code>数组中,如果在，将该对象存入list中返回
     * @throws NullPointerException
     *             if Validator.isNullOrEmpty(objectCollection) || Validator.isNullOrEmpty(propertyName)
     */
    @SuppressWarnings("unchecked")
    public static <O, V> List<O> select(Collection<O> objectCollection,String propertyName,V...values) throws NullPointerException{
        if (Validator.isNullOrEmpty(objectCollection)){
            throw new NullPointerException("objectCollection is null or empty!");
        }

        if (Validator.isNullOrEmpty(propertyName)){
            throw new NullPointerException("propertyName is null or empty!");
        }
        Predicate predicate = getArrayContainsPredicate(propertyName, values);
        return (List<O>) org.apache.commons.collections.CollectionUtils.select(objectCollection, predicate);
    }

    /**
     * 调用 {@link PropertyUtil#getProperty(Object, String)} 获得 <code>propertyName</code>的值，判断是否 {@link ArrayUtil#isContain(V[], V)} 在
     * <code>values</code>数组中.
     *
     * @param <V>
     *            the value type
     * @param propertyName
     *            the property name
     * @param values
     *            the values
     * @return the array predicate
     * @see com.feilong.commons.core.bean.PropertyUtil#getProperty(Object, String)
     * @see com.feilong.commons.core.util.ArrayUtil#isContain(V[], V)
     * @since 1.0.9
     */
    //@SafeVarargs
    private static <V> Predicate getArrayContainsPredicate(final String propertyName,final V...values){
        Predicate predicate = new Predicate(){

            @Override
            public boolean evaluate(Object object){
                V property = PropertyUtil.getProperty(object, propertyName);
                return ArrayUtil.isContain(values, property);
            }
        };
        return predicate;
    }

    /**
     * 获得 object equals predicate.
     *
     * @param <V>
     *            the value type
     * @param propertyName
     *            the property name
     * @param value
     *            the value
     * @return the object equals predicate
     * @since 1.0.9
     */
    private static <V> Predicate getObjectEqualsPredicate(final String propertyName,final V value){
        Predicate predicate = new Predicate(){

            @Override
            public boolean evaluate(Object object){
                V property = PropertyUtil.getProperty(object, propertyName);
                return ObjectUtil.equals(property, value, true);
            }
        };
        return predicate;
    }

    /**
     * Select.
     *
     * @param <O>
     *            the generic type
     * @param objectCollection
     *            the object collection
     * @param predicate
     *            the predicate
     * @return the list< o>
     * @throws NullPointerException
     *             the null pointer exception
     */
    @SuppressWarnings("unchecked")
    public static <O> List<O> select(Collection<O> objectCollection,Predicate predicate) throws NullPointerException{
        if (Validator.isNullOrEmpty(objectCollection)){
            throw new NullPointerException("objectCollection is null or empty!");
        }

        return (List<O>) org.apache.commons.collections.CollectionUtils.select(objectCollection, predicate);
    }

    /**
     * 循环遍历 <code>objectCollection</code> ,返回 当bean propertyName 属性值不 equals 特定value 时候的list.
     *
     * @param <O>
     *            the generic type
     * @param <V>
     *            the value type
     * @param objectCollection
     *            the object list
     * @param propertyName
     *            the property name
     * @param value
     *            the value
     * @return the property value list
     * @throws NullPointerException
     *             the null pointer exception
     * @see org.apache.commons.collections.CollectionUtils#selectRejected(Collection, org.apache.commons.collections.Predicate)
     */
    public static <O, V> List<O> selectRejected(Collection<O> objectCollection,String propertyName,V value) throws NullPointerException{
        Object[] values = { value };
        return selectRejected(objectCollection, propertyName, values);
    }

    /**
     * 循环遍历 <code>objectCollection</code> ,返回 当bean propertyName 属性值 都不在values 时候的list.
     *
     * @param <O>
     *            the generic type
     * @param <V>
     *            the value type
     * @param objectCollection
     *            the object collection
     * @param propertyName
     *            the property name
     * @param values
     *            the values
     * @return the list< o>
     * @throws NullPointerException
     *             the null pointer exception
     */
    @SuppressWarnings("unchecked")
    public static <O, V> List<O> selectRejected(Collection<O> objectCollection,String propertyName,V...values) throws NullPointerException{
        if (Validator.isNullOrEmpty(objectCollection)){
            throw new NullPointerException("objectCollection is null or empty!");
        }

        if (Validator.isNullOrEmpty(propertyName)){
            throw new NullPointerException("propertyName is null or empty!");
        }
        Predicate predicate = getArrayContainsPredicate(propertyName, values);
        return (List<O>) org.apache.commons.collections.CollectionUtils.selectRejected(objectCollection, predicate);
    }

    /**
     * 解析对象集合,以 <code>keyPropertyName</code>属性值为key， <code>valuePropertyName</code>属性值为值，组成map返回<br>
     * 
     * <p>
     * 注意:返回的是 {@link LinkedHashMap}
     * </p>
     * <br>
     * 使用 {@link com.feilong.commons.core.bean.PropertyUtil#getProperty(Object, String)}取到对象特殊属性. <br>
     * 支持属性级联获取,支付获取数组,集合,map,自定义bean等属性
     * 
     * <h3>使用示例:</h3>
     * 
     * <blockquote>
     * 
     * <pre>
     * List&lt;User&gt; testList = new ArrayList&lt;User&gt;();
     * testList.add(new User(&quot;张飞&quot;, 23));
     * testList.add(new User(&quot;关羽&quot;, 24));
     * testList.add(new User(&quot;刘备&quot;, 25));
     * 
     * Map&lt;String, Integer&gt; map = CollectionsUtil.getFieldValueMap(testList, &quot;name&quot;, &quot;age&quot;);
     * 
     * 返回 :
     * 
     * "关羽": 24,
     * "张飞": 23,
     * "刘备": 25
     * </pre>
     * 
     * </blockquote>
     * 
     *
     * @param <K>
     *            the key type
     * @param <V>
     *            the value type
     * @param <O>
     *            可迭代对象类型 generic type
     * @param objectCollection
     *            任何可以迭代的对象
     * @param keyPropertyName
     *            the key property name
     * @param valuePropertyName
     *            the value property name
     * @return 解析迭代集合,取到对象特殊属性,拼成List(ArrayList)
     * @throws NullPointerException
     *             if Validator.isNullOrEmpty(objectCollection) or Validator.isNullOrEmpty(propertyName) or
     *             Validator.isNullOrEmpty(valuePropertyName)
     * @throws BeanUtilException
     *             the bean util exception
     * @see com.feilong.commons.core.bean.BeanUtil#getProperty(Object, String)
     * @see org.apache.commons.beanutils.PropertyUtils#getProperty(Object, String)
     * @since jdk1.5
     */
    public static <K, V, O> Map<K, V> getPropertyValueMap(Collection<O> objectCollection,String keyPropertyName,String valuePropertyName)
                    throws NullPointerException,BeanUtilException{
        if (Validator.isNullOrEmpty(objectCollection)){
            throw new NullPointerException("objectCollection is null or empty!");
        }

        if (Validator.isNullOrEmpty(keyPropertyName)){
            throw new NullPointerException("keyPropertyName is null or empty!");
        }

        if (Validator.isNullOrEmpty(valuePropertyName)){
            throw new NullPointerException("valuePropertyName is null or empty!");
        }

        Map<K, V> map = new LinkedHashMap<K, V>();

        for (O bean : objectCollection){
            @SuppressWarnings("unchecked")
            K key = (K) PropertyUtil.getProperty(bean, keyPropertyName);
            @SuppressWarnings("unchecked")
            V value = (V) PropertyUtil.getProperty(bean, valuePropertyName);

            map.put(key, value);
        }
        return map;
    }

    /**
     * Group 对象(如果propertyName 存在相同的值，那么这些值，将会以list的形式 put到map中).
     *
     * @param <T>
     *            注意，此处的T其实是 Object 类型， 需要区别对待，比如从excel中读取的类型是String，那么就不能简简单单的使用Integer来接收， 因为不能强制转换
     * @param <O>
     *            the generic type
     * @param objectCollection
     *            the object list
     * @param propertyName
     *            对面里面属性的名称
     * @return the map< t, list< o>>
     * @throws BeanUtilException
     *             the bean util exception
     * @throws NullPointerException
     *             if Validator.isNullOrEmpty(propertyName)
     * @see com.feilong.commons.core.bean.PropertyUtil#getProperty(Object, String)
     * @see com.feilong.commons.core.util.ArrayUtil#group(O[], String)
     * @see #groupOne(Collection, String)
     * @since 1.0.8
     */
    public static <T, O> Map<T, List<O>> group(Collection<O> objectCollection,String propertyName) throws BeanUtilException,
                    NullPointerException{

        if (Validator.isNullOrEmpty(objectCollection)){
            throw new NullPointerException("objectCollection can't be null/empty!");
        }

        if (Validator.isNullOrEmpty(propertyName)){
            throw new NullPointerException("the propertyName is null or empty!");
        }

        //视需求  可以换成 HashMap 或者TreeMap
        Map<T, List<O>> map = new LinkedHashMap<T, List<O>>(objectCollection.size());

        for (O o : objectCollection){
            T t = PropertyUtil.getProperty(o, propertyName);
            List<O> valueList = map.get(t);
            if (null == valueList){
                valueList = new ArrayList<O>();
            }
            valueList.add(o);
            map.put(t, valueList);
        }
        return map;
    }

    /**
     * Group count.
     *
     * @param <T>
     *            the generic type
     * @param <O>
     *            the generic type
     * @param objectCollection
     *            the object collection
     * @param propertyName
     *            the property name
     * @return 返回的是 {@link LinkedHashMap}
     * @throws BeanUtilException
     *             the bean util exception
     * @throws NullPointerException
     *             the null pointer exception
     */
    public static <T, O> Map<T, Integer> groupCount(Collection<O> objectCollection,String propertyName) throws BeanUtilException,
                    NullPointerException{

        if (Validator.isNullOrEmpty(objectCollection)){
            throw new NullPointerException("objectCollection can't be null/empty!");
        }

        if (Validator.isNullOrEmpty(propertyName)){
            throw new NullPointerException("the propertyName is null or empty!");
        }

        Map<T, Integer> map = new LinkedHashMap<T, Integer>();

        for (O o : objectCollection){
            T t = PropertyUtil.getProperty(o, propertyName);
            Integer count = map.get(t);
            if (null == count){
                count = 0;
            }
            count = count + 1;
            map.put(t, count);
        }
        return map;
    }

    /**
     * Group one(map只put第一个匹配的元素).
     *
     * @param <T>
     *            the generic type
     * @param <O>
     *            the generic type
     * @param objectCollection
     *            the object collection
     * @param propertyName
     *            the property name
     * @return the map< t, o>
     * @throws BeanUtilException
     *             the bean util exception
     * @throws NullPointerException
     *             the null pointer exception
     * @see #group(Collection, String)
     * @since 1.0.8
     */
    public static <T, O> Map<T, O> groupOne(Collection<O> objectCollection,String propertyName) throws BeanUtilException,
                    NullPointerException{

        if (Validator.isNullOrEmpty(objectCollection)){
            throw new NullPointerException("objectCollection can't be null/empty!");
        }

        if (Validator.isNullOrEmpty(propertyName)){
            throw new NullPointerException("the propertyName is null or empty!");
        }

        //视需求  可以换成 HashMap 或者TreeMap
        Map<T, O> map = new LinkedHashMap<T, O>(objectCollection.size());

        for (O o : objectCollection){
            T t = PropertyUtil.getProperty(o, propertyName);
            O valueList = map.get(t);
            if (null == valueList){
                map.put(t, o);
            }else{
                if (log.isDebugEnabled()){
                    log.debug("when propertyName:{},multiple value:{},Abandoned except the first value outside.", propertyName, valueList);
                }
            }
        }
        return map;
    }

    /**
     * 算术平均值.
     *
     * @param <O>
     *            the generic type
     * @param objectCollection
     *            the object collection
     * @param scale
     *            平均数值的精度
     * @param propertyNames
     *            需要计算平均值的对象属性名称
     * @return the map< string, list< o>>
     * @throws BeanUtilException
     *             the bean util exception
     * @throws NullPointerException
     *             the null pointer exception
     * @see #sum(Collection, String...)
     */
    public static <O> Map<String, Number> avg(Collection<O> objectCollection,int scale,String...propertyNames) throws BeanUtilException,
                    NullPointerException{

        //总分
        Map<String, Number> sumMap = sum(objectCollection, propertyNames);

        int size = objectCollection.size();

        //视需求  可以换成 HashMap 或者TreeMap
        Map<String, Number> map = new LinkedHashMap<String, Number>(size);

        for (Map.Entry<String, Number> entry : sumMap.entrySet()){
            String key = entry.getKey();
            Number value = entry.getValue();

            map.put(key, NumberUtil.getDivideValue(new BigDecimal(value.toString()), size, scale));
        }

        return map;
    }

    /**
     * 总和，计算集合对象内指定的属性的总和.
     *
     * @param <O>
     *            the generic type
     * @param objectCollection
     *            the object collection
     * @param propertyNames
     *            the property names
     * @return the map< string, list< o>>
     * @throws BeanUtilException
     *             the bean util exception
     * @throws NullPointerException
     *             the null pointer exception
     */
    public static <O> Map<String, Number> sum(Collection<O> objectCollection,String...propertyNames) throws BeanUtilException,
                    NullPointerException{

        if (Validator.isNullOrEmpty(objectCollection)){
            throw new NullPointerException("objectCollection can't be null/empty!");
        }

        if (Validator.isNullOrEmpty(propertyNames)){
            throw new NullPointerException("propertyNames is null or empty!");
        }

        //**************************************************************************
        int size = objectCollection.size();

        //总分
        Map<String, Number> sumMap = new LinkedHashMap<String, Number>(size);

        for (O o : objectCollection){
            for (String propertyName : propertyNames){

                //取到数据
                Number propertyValue = PropertyUtil.getProperty(o, propertyName);

                //map中的数值
                Number mapPropertyNameValue = sumMap.get(propertyName);
                if (null == mapPropertyNameValue){
                    mapPropertyNameValue = 0;
                }
                sumMap.put(propertyName, NumberUtil.getAddValue(mapPropertyNameValue, propertyValue));
            }
        }

        //**************************************************************************
        return sumMap;
    }
}
