import {merge} from "lodash";
import {ApiData} from "../../../hooks/useApi.ts";
import {MergedData} from "./HomeDataContext.tsx";

export default function getMergedData(registrationsData: ApiData, deregistrationsData: ApiData, airPollutionData: ApiData): ApiData<MergedData> {
  if (!registrationsData.data || !deregistrationsData.data || !airPollutionData.data) {
    return {
      data: null,
      loaded: false,
      error: registrationsData.error || deregistrationsData.error || airPollutionData.error,
    };
  }

  const registrations = registrationsData.data;
  const deregistrations = deregistrationsData.data;
  const airPollution = airPollutionData.data;

  const registrationsChanged: any = {}

  for (const [year, data] of Object.entries(registrations)) {
    for (const [areaCode, count] of Object.entries(data as any)) {
      const lowerAreaCode = areaCode.toLowerCase();
      if (!registrationsChanged[year]) {
        registrationsChanged[year] = {};
      }
      if (!registrationsChanged[year][lowerAreaCode]) {
        registrationsChanged[year][lowerAreaCode] = {};
      }
      registrationsChanged[year][lowerAreaCode].registrations = count;
    }
  }

  const deregistrationsChanged: any = {}

  for (const [year, data] of Object.entries(deregistrations)) {
    for (const [areaCode, count] of Object.entries(data as any)) {
      const lowerAreaCode = areaCode.toLowerCase();
      if (!deregistrationsChanged[year]) {
        deregistrationsChanged[year] = {};
      }
      if (!deregistrationsChanged[year][lowerAreaCode]) {
        deregistrationsChanged[year][lowerAreaCode] = {};
      }
      deregistrationsChanged[year][lowerAreaCode].deregistrations = count;
    }
  }

  return {
    data: merge({}, registrationsChanged, deregistrationsChanged, airPollution),
    loaded: registrationsData.loaded && deregistrationsData.loaded && airPollutionData.loaded,
    error: registrationsData.error || deregistrationsData.error || airPollutionData.error,
  };
}