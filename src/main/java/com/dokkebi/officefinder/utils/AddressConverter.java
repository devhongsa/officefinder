package com.dokkebi.officefinder.utils;

import com.dokkebi.officefinder.entity.office.OfficeLocation;
import com.dokkebi.officefinder.entity.type.Address;
import org.springframework.util.StringUtils;

public class AddressConverter {

  public static String getAddress(OfficeLocation location) {
    StringBuilder sb = new StringBuilder();

    Address address = location.getAddress();

    sb.append(address.getLegion()).append(" ");
    sb.append(address.getCity()).append(" ");
    sb.append(address.getTown()).append(" ");

    if (StringUtils.hasText(address.getVillage())) {
      sb.append(address.getVillage()).append(" ");
    }

    sb.append(address.getStreet()).append(" ");
    sb.append(address.getBuildingNumber());

    return sb.toString();
  }
}
