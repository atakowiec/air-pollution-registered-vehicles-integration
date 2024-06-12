import {ApiData} from "../../../hooks/useApi.ts";
import {MergedData, VoivodeshipData} from "./HomeDataContext.tsx";

export default function getCumulativeData(mergedData: ApiData<MergedData>): ApiData<MergedData> {
  if (!mergedData.loaded || !mergedData.data) {
    return {
      data: null,
      loaded: false,
      error: mergedData.error,
    };
  }

  // count occurences of air pollution for each voivodeship, so we can calculate average air pollution for each voivodeship
  const counts: { [year: string]: { [voivodeship: string]: { [indicator: string]: number } } } = {};
  const countsSoFar: { [voivodeship: string]: { [indicator: string]: number } } = {};
  const sumSoFar: { [voivodeship: string]: { [indicator: string]: number } } = {};
  const result: MergedData = {};

  for (const [year, data] of Object.entries(mergedData.data)) {
    if (!result[year]) {
      result[year] = {};
      counts[year] = {};
    }

    for (const voivodeship of Object.keys(data)) {
      if (!result[year][voivodeship]) {
        result[year][voivodeship] = {};
        counts[year][voivodeship] = {};
      }

      if (!sumSoFar[voivodeship]) {
        sumSoFar[voivodeship] = {};
        countsSoFar[voivodeship] = {};
      }

      for (const anyKey of Object.keys(data[voivodeship])) {
        const key = anyKey as keyof VoivodeshipData;
        if (!result[year][voivodeship][key]) {
          result[year][voivodeship][key] = 0;
        }

        if (!sumSoFar[voivodeship][key]) {
          sumSoFar[voivodeship][key] = 0;
        }

        sumSoFar[voivodeship][key]! += data[voivodeship][key]!;
        result[year][voivodeship][key]! = sumSoFar[voivodeship][key]!;
        if (key !== "registrations" && key !== "deregistrations") {
          if (!counts[year][voivodeship]) {
            counts[year][voivodeship] = {};
          }

          if (!countsSoFar[voivodeship][key]) {
            countsSoFar[voivodeship][key] = 0;
          }
          countsSoFar[voivodeship][key]! += 1;

          result[year][voivodeship][key]! /= countsSoFar[voivodeship][key]!;
        }
      }

      // copy registrations and deregistrations if they are not present in current year
      if (!result[year][voivodeship]["registrations"]) {
        result[year][voivodeship]["registrations"] = sumSoFar[voivodeship]["registrations"];
      }
      if (!result[year][voivodeship]["deregistrations"]) {
        result[year][voivodeship]["deregistrations"] = sumSoFar[voivodeship]["deregistrations"];
      }
    }
  }

  return {
    data: result,
    loaded: true,
    error: false,
  };
}