import {ApiData} from "../../../hooks/useApi.ts";
import style from "../Home.module.scss";
import {isEmpty} from "../../../util/utils.ts";
import LoadingSpinner from "../../../components/LoadingSpinner.tsx";

export interface PropsWithApiData<T = any> {
  apiData: ApiData<T>
}

interface Props {
  apiData: ApiData | ApiData[];
  backgroundType?: "none" | "visible"
}

/**
 * Component that displays the status of the chart data
 * e.g. if the data is being loaded or failed to load
 *
 * @param apiData - data from the API that is used to determine the status of the chart data
 * @param backgroundType - type of the background that should be displayed
 */
export default function ChartStatusOverlay({apiData, backgroundType = "visible"}: Props) {
  let loading = false;
  let error = false;
  let noData = false;

  for (const singleApiData of Array.isArray(apiData) ? apiData : [apiData]) {
    // if data is loaded correctly, we don't need to display anything
    if (singleApiData.loaded && singleApiData.data && !isEmpty(singleApiData.data))
      return null;

    if (!singleApiData.loaded)
      loading = true;

    if (singleApiData.error)
      error = true;

    if (isEmpty(singleApiData.data))
      noData = true;
  }

  return (
    <div className={`${style.chartStatusOverlay} ${style[`background_${backgroundType}`]}`}>
      {error ? "Wystąpił błąd podczas ładowania danych"
        : loading ? <LoadingSpinner/>
          : noData ? "Brak danych do wyświetlenia" : ""}
    </div>
  )
}