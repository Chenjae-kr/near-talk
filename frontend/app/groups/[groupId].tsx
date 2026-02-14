import { View, Text } from "react-native";
import { useLocalSearchParams } from "expo-router";

export default function GroupDetailScreen() {
  const { groupId } = useLocalSearchParams<{ groupId: string }>();

  return (
    <View className="flex-1 items-center justify-center">
      <Text className="text-lg font-bold">모임 상세</Text>
      <Text className="mt-2 text-gray-500">Group ID: {groupId}</Text>
    </View>
  );
}
