import {ProgressData, ProgressStatus} from "../../../../hooks/useProgress.ts";
import {useMemo} from "react";
import {formatDate, formatDuration, formatNumber, round} from "../../../../util/utils.ts";
import Container from "react-bootstrap/Container";
import {ProgressBar} from "react-bootstrap";
import ImportErrors from "./ImportErrors.tsx";

interface ImportProgressInfoProps {
  progress: ProgressData<ImportProgressData> | undefined
  message?: string
}

export interface ImportProgressData {
  total: number;
  saved: number;
  read: number;
  loaded: boolean;
  startTime?: number
  readTime?: number
  loadTime?: number
  endTime?: number
  readErrors?: number
  readErrorsList?: { vehicleId: string, errorMessage: string, columnData: { [key: string]: string }, line: string }[]
  saveErrors?: number
  saveErrorsList?: { errorMessage: string, data: string }[]
}

interface CalculatedData {
  status: ProgressStatus
  total?: number;
  saved?: number;
  read?: number;
  readPerSecond?: number;
  savedPerSecond?: number;
  loaded?: boolean;
  startTime?: string;
  loadTime?: string;
  endTime?: string;
  readTime?: string;
  totalTime?: string;
  readProgress?: number;
  saveProgress?: number;
  readApproxTime?: string;
  saveApproxTime?: string;
}

function translateStatus(status: ProgressStatus) {
  switch (status) {
    case "NOT_STARTED":
      return "Nie rozpoczęto"
    case "IN_PROGRESS":
      return "W trakcie"
    case "FINISHED":
      return "Zakończono"
    case "FAILED":
      return "Niepowodzenie"
  }
}

export default function ImportProgressInfo({progress}: ImportProgressInfoProps) {
  const calculatedData: CalculatedData = useMemo((): CalculatedData => {
    if (!progress?.data || progress.status === "NOT_STARTED") return {status: "NOT_STARTED"}

    const total = progress.data.total ?? 0
    const saved = progress.data.saved ?? 0
    const read = progress.data.read ?? 0
    const loaded = progress.data.loaded ?? false

    const readPerSecond = round(progress.data.read / ((progress.data.readTime ?? Date.now()) - (progress.data.startTime ?? Date.now())) * 1000)
    const savedPerSecond = round(progress.data.saved / ((progress.data.endTime ?? Date.now()) - (progress.data.startTime ?? Date.now())) * 1000)

    const saveApproxTime = savedPerSecond ? formatDuration((total - saved) / savedPerSecond * 1000) : undefined
    const readApproxTime = readPerSecond ? formatDuration((total - read) / readPerSecond * 1000) : undefined
    const totalTime = formatDuration((progress.data.endTime ?? Date.now()) - (progress.data.startTime ?? Date.now()))

    return {
      status: progress.status,
      total,
      saved,
      read,
      loaded,
      readPerSecond,
      savedPerSecond,
      readApproxTime,
      saveApproxTime,
      totalTime,
      startTime: formatDate(progress.data.startTime),
      loadTime: formatDate(progress.data.loadTime),
      endTime: formatDate(progress.data.endTime),
      readTime: formatDate(progress.data.readTime),
      readProgress: total === 0 ? 0 : read / total * 100,
      saveProgress: total === 0 ? 0 : saved / total * 100
    }
  }, [progress])

  if (!progress || calculatedData.status == "NOT_STARTED") return null
  return (
    <>
      <Container className={"text-center px-0 mt-2 col-12 col-md-8 col-xl-6 col-xxl-5 gap-5 mb-5 pb-5"}>
        <Container className={"bg-light rounded p-2 mb-2"}>
          <h3>Postęp importu</h3>
          <h5>Status: {translateStatus(calculatedData.status)}</h5>
          <p className={"mt-4"}>
            Rozpoczęto: {calculatedData.startTime ?? "-"}<br/>
            Zakończono: {calculatedData.endTime ?? "-"}<br/>
            Czas trwania: {calculatedData.totalTime ?? "-"}
          </p>
        </Container>
        <Container className={"bg-light rounded p-4 pb-2 mb-2"}>
          <h4>Odczytanie danych</h4>
          <p>
            Rozpoczęto: {calculatedData.loadTime ?? "-"}<br/>
            Zakończono: {calculatedData.readTime ?? "-"}
          </p>
          <ProgressBar now={calculatedData.readProgress!} label={`${Math.round(calculatedData.readProgress!)}%`}/>
          <p className={"mt-3"}>
            Odczytano: {formatNumber(calculatedData.read!)} z {formatNumber(calculatedData.total!)} rekordów
            ({calculatedData.readApproxTime ?? "---"})<br/>
            Prędkość odczytu: {calculatedData.readPerSecond} rekordów/s
          </p>
          <ImportErrors errors={progress.data?.readErrorsList} errorsCount={progress.data?.readErrors}/>
        </Container>
        <Container className={"bg-light rounded p-4 pb-2 mb-2"}>
          <h4 className={"mb-4"}>Zapis danych</h4>
          <ProgressBar now={calculatedData.saveProgress!} label={`${Math.round(calculatedData.saveProgress!)}%`}/>
          <p className={"mt-3"}>
            Zapisano: {formatNumber(calculatedData.saved!)} z {formatNumber(calculatedData.total!)} rekordów
            ({calculatedData.saveApproxTime ?? "---"})<br/>
            Prędkość zapisu: {calculatedData.savedPerSecond} rekordów/s
          </p>
          <ImportErrors errors={progress.data?.saveErrorsList} errorsCount={progress.data?.saveErrors}/>
        </Container>
      </Container>
    </>
  )
}