package com.dax.debarauamea.Objects;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;

public class DTOProducts extends RealmObject implements Serializable {

    public String BARCODE;
    public String NAME;
    public Date EXPIRATION_DATE;
    public Boolean HAS_EXPIRATION_DATE;
    public float QUANTITY;
}
