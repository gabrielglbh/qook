import { OBJ_TAG_KEYWORDS, OBJ_TAG_NAME } from "../../components/Globals";
import Tag from "./Tag";

class TagDto {
  id: string;
  text: string;
  keywords: string[];
  color: number;

  constructor(
    id: string = "",
    text: string = "",
    keywords: string[] = [],
    color: number = -1,
  ) {
    this.id = id;
    this.text = text;
    this.keywords = keywords;
    this.color = color;
  }
}

export const tagDtoToDomain = (dto: TagDto) => {
  return new Tag(
    dto.id,
    dto.text,
  );
}

export const tagDtoToMap = (dto: TagDto) => {
  return {
    "id": dto.id,
    [OBJ_TAG_NAME]: dto.text,
    [OBJ_TAG_KEYWORDS]: dto.keywords,
    "color": -1,
  };
}

export default TagDto;
